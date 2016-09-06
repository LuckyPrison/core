package com.ulfric.core.achievement;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.data.scope.ScopeListener;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.string.Named;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.string.ChatUtils;

public final class Achievement implements Named, ScopeListener<UUID> {

	Achievement(String name, String description, String code, MaterialData item, int min, Achievement parent)
	{
		this.name = name;
		this.description = description;
		this.item = item;
		this.min = min;

		if (parent != null)
		{
			this.parent = parent;
			parent.child = this;
		}

		DocumentStore store = PlayerUtils.getPlayerData();

		DataManager.get().ensureTableCreated(store, "achievements");

		this.subscription = store
				.multi(Integer.class, PlayerScopes.ONLINE, new DataAddress<>("achievements", code))
				.blockOnSubscribe(true)
				.blockOnUnsubscribe(true)
				.subscribe();

		PlayerScopes.ONLINE.addListener(this);
	}

	private final String name;
	private final String description;
	private final MaterialData item;
	private final int min;
	private final MultiSubscription<UUID, Integer> subscription;
	private final Map<UUID, Counter> counters = Maps.newHashMap();
	private Achievement child;
	private Achievement parent;

	public Achievement getNextTier()
	{
		return this.child;
	}

	public Achievement getParent()
	{
		return this.parent;
	}

	@Override
	public void onAddition(UUID uuid)
	{
		Integer count = this.subscription.get(uuid).getValue();

		if (count == null) return;

		this.counters.put(uuid, new Counter(count));
	}

	@Override
	public void onRemove(UUID uuid)
	{
		Counter counter = this.counters.remove(uuid);

		if (counter == null || !counter.hasBeenChanged()) return;

		try
		{
			this.subscription.retrieveForeignContainer(uuid, (container) -> container.setValue(counter.toInt())).get();
		}
		catch (InterruptedException|ExecutionException exception)
		{
			exception.printStackTrace();
		}
	}

	public void increment(UUID uuid)
	{
		this.increment(uuid, 1);
	}

	public void increment(UUID uuid, int amount)
	{
		Counter counter = this.counters.get(uuid);

		int start = 0;

		if (counter == null)
		{
			counter = new Counter(amount, true);

			this.counters.put(uuid, counter);
		}
		else
		{
			start = counter.toInt();
		}

		int value = counter.toInt();

		if (value < this.min) return;

		if (value > this.min || start >= this.min)
		{
			int vm = value - this.min;

			if (vm > 0)
			{
				Achievement next = this.getNextTier();

				if (next != null)
				{
					next.increment(uuid, vm);
				}
			}

			if (start >= this.min)
			{
				return;
			}
		}

		// TODO send update message
	}

	public int getProgress(UUID uuid)
	{
		Counter counter = this.counters.get(uuid);

		if (counter == null) return 0;

		return counter.toInt();
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public ItemStack toItem(UUID uuid)
	{
		ItemStack to = this.item.toItem(1);

		ItemMeta meta = to.getMeta();

		List<String> lore = Lists.newArrayList();

		lore.add("");

		int progress = this.getProgress(uuid);

		String wrap = WordUtils.wrap(this.description, 15, "<n>", false);

		for (String part : wrap.split("<n>"))
		{
			lore.add(ChatUtils.color("&f&o") + part);
		}

		if (lore.size() > 1)
		{
			lore.add("");
		}

		if (progress >= this.min)
		{
			lore.add(ChatUtils.color("&a") + "- Achievement Unlocked -");
		}
		else
		{
			if (this.min != 1)
			{
				int percentage = (int) (((float) progress / this.min) * 100);

				lore.add(Strings.format(ChatUtils.color("&eProgress: &7{0}%"), percentage));
			}
			else
			{
				lore.add(ChatUtils.color("&eProgress: &7INCOMPLETE"));
			}

			Achievement parentLocal = this.getParent();

			if (parentLocal != null)
			{
				lore.add("");

				do
				{
					lore.add(ChatUtils.color("&cRequires: &7") + parentLocal.getName());

					parentLocal = parentLocal.getParent();
				}
				while (parentLocal != null);
			}
		}

		meta.setDisplayName(ChatUtils.color("&7") + this.name);
		meta.setAllLore(lore);

		to.setMeta(meta);

		return to;
	}

	public boolean isComplete(UUID uuid)
	{
		Counter counter = this.counters.get(uuid);

		if (counter == null) return false;

		return counter.toInt() >= this.min;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Achievement>
	{
		Builder() { }

		private String name;
		private String desc;
		private String code;
		private MaterialData item;
		private int min = 1;
		private Achievement parent;

		@Override
		public Achievement build()
		{
			Validate.notNull(this.name);
			Validate.notNull(this.code);
			Validate.notNull(this.item);
			Validate.notNull(this.desc);

			return new Achievement(this.name, this.desc, this.code, this.item, this.min, this.parent);
		}

		public Builder setName(String name)
		{
			Validate.notBlank(name);

			this.name = name;

			return this;
		}

		public Builder setDescription(String description)
		{
			Validate.notBlank(description);

			this.desc = description;

			return this;
		}

		public Builder setCode(String code)
		{
			Validate.notBlank(code);

			this.code = code.trim().toLowerCase();

			return this;
		}

		public Builder setItem(MaterialData item)
		{
			Validate.notNull(item);

			this.item = item;

			return this;
		}

		public Builder setMin(int min)
		{
			Validate.isTrue(min > 0);

			this.min = min;

			return this;
		}

		public Builder setParent(Achievement parent)
		{
			this.parent = parent;

			return this;
		}
	}

}