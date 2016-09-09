package com.ulfric.core.crate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.core.reward.Reward;
import com.ulfric.data.DataContainer;
import com.ulfric.data.MultiSubscription;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.string.Named;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.location.Location;

final class Crate implements Named {

	public static final Argument ARGUMENT = Argument.builder().addResolver((sender, arg) -> ModuleCrates.INSTANCE.getCrates().stream().filter(crate -> crate.getName().equalsIgnoreCase(arg)).findFirst().orElse(null)).setPath("crate").setUsage("specify-crate").build();

	private final MultiSubscription<UUID, Document> subscription;
	private final Map<UUID, Integer> cache = Maps.newHashMap();

	private final int id;
	private final String name;
	private final List<Reward> rewards;
	private final List<Location> locations;

	private Crate(MultiSubscription<UUID, Document> subscription, int id, String name, List<Reward> rewards, List<Location> locations)
	{
		this.subscription = subscription;
		this.id = id;
		this.name = name;
		this.rewards = rewards;
		this.locations = locations;
	}

	public int getKeys(OfflinePlayer player)
	{
		return this.cache.computeIfAbsent(player.getUniqueId(), (uuid) ->
		{
			Document document = this.subscription.get(uuid).getValue();

			return document.getInteger("keys." + this.getName() + ".amount", 0);
		});
	}

	public void giveKeys(OfflinePlayer player, int amount)
	{
		int current = this.getKeys(player);

		this.cache.put(player.getUniqueId(), current + amount);

		DataContainer<UUID, Document> container = this.subscription.get(player.getUniqueId());

		MutableDocument document = new SimpleDocument(container.getValue().deepCopy());

		document.set("keys." + this.getName() + ".amount", current + amount);

		try
		{
			container.setValue(document).get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
	}

	public void removeKeys(OfflinePlayer player, int amount)
	{
		this.giveKeys(player, -amount);
	}

	public boolean canOpen(OfflinePlayer player)
	{
		return this.getKeys(player) > 0;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Crate> {

		private String name;
		private List<Reward> rewards = Lists.newArrayList();
		private List<Location> locations = Lists.newArrayList();
		private Integer id;

		public Builder withName(String name)
		{
			this.name = name;

			return this;
		}

		public Builder withId(int id)
		{
			this.id = id;

			return this;
		}

		public Builder withReward(Reward reward)
		{
			this.rewards.add(reward);

			return this;
		}

		public Builder withLocation(Location location)
		{
			this.locations.add(location);

			return this;
		}

		@Override
		public Crate build()
		{
			Validate.notNull(this.name);
			Validate.notNull(this.id);
			Validate.notEmpty(this.rewards);
			Validate.notEmpty(this.locations);

			return new Crate(ModuleCrates.INSTANCE.getSubscription(), this.id, this.name, this.rewards, this.locations);
		}

	}

}
