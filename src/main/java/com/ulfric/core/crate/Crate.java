package com.ulfric.core.crate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.coffee.tuple.Weighted;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.location.LocationUtils;
import com.ulfric.lib.craft.note.Sound;
import com.ulfric.lib.craft.panel.Panel;
import com.ulfric.lib.craft.panel.standard.StandardPanel;
import com.ulfric.lib.craft.string.ChatUtils;

final class Crate extends NamedBase {

	public static final Argument ARGUMENT = Argument.builder().addResolver((sender, arg) -> ModuleCrates.INSTANCE.getCrates().stream().filter(crate -> crate.getName().equalsIgnoreCase(arg)).findFirst().orElse(null)).setPath("crate").setUsage("specify-crate").build();

	private final MultiSubscription<UUID, Document> subscription;
	private final Map<UUID, Integer> cache = Maps.newHashMap();

	private final int id;
	private final List<Weighted<IconnedReward>> rewards;
	private final List<Location> locations;

	private final int totalWeight;
	private final int totalSize;

	private Crate(MultiSubscription<UUID, Document> subscription, int id, String name, List<Weighted<IconnedReward>> rewards, List<Location> locations)
	{
		super(name);

		this.subscription = subscription;
		this.id = id;
		this.rewards = rewards;
		this.locations = locations;

		this.totalWeight = this.rewards.stream().mapToInt(Weighted::getWeight).sum();
		this.totalSize = rewards.size();
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

	private int getInventorySize()
	{
		return NumberUtils.roundUp(this.rewards.size(), 9) + 18;
	}

	public boolean atLocation(Location location)
	{
		return this.locations.stream().filter(pred -> LocationUtils.sameBlock(location, pred)).findFirst().orElse(null) != null;
	}

	public void open(Player player)
	{
		new CratePanel(player).open();
	}

	private final class Open implements Runnable {

		private static final int DELAY_TICKS = 44;

		private final Player player;
		private final CratePanel panel;
		private final int rewardSlot;
		private final Reward reward;

		private final Iterator<Integer> iTop;
		private final Iterator<Integer> iBottom;

		private int id;

		private Open(Player player, CratePanel panel, Reward reward, int rewardSlot)
		{
			this.player = player;
			this.panel = panel;
			this.reward = reward;
			this.rewardSlot = rewardSlot;

			List<Integer> top = Lists.newArrayList();
			List<Integer> bottom = Lists.newArrayList();

			for (int x = 0; x < this.rewardSlot; x++)
			{
				top.add(x);
			}

			for (int x = Crate.this.totalSize; x > this.rewardSlot; x--)
			{
				bottom.add(x);
			}

			this.iTop = top.iterator();
			this.iBottom = bottom.iterator();

			this.panel.getPanel().withCloseConsumer(event ->
			{
				ThreadUtils.cancel(this.id);

				if (this.reward != null)
				{
					this.reward.give(player, "crate-reward", Crate.this.getName());
				}
			});

			this.id = ThreadUtils.runRepeating(this, 3);
		}

		@Override
		public void run()
		{
			this.player.playSound(Sound.of("ITEM_BREAK"), 6, 10);

			StandardPanel standardPanel = this.panel.getPanel();

			boolean flag = false;

			if (this.iTop.hasNext())
			{
				flag = true;

				standardPanel.setItem(iTop.next(), ItemUtils.getItem(Material.of("AIR")));
			}

			if (this.iBottom.hasNext())
			{
				flag = true;

				standardPanel.setItem(iBottom.next(), ItemUtils.getItem(Material.of("AIR")));
			}

			if (flag)
			{
				return;
			}

			this.player.playSound(player.getLocation(), Sound.of("ARROW_HIT"), 6, 7);

			this.player.closeInventory();
		}

	}

	private final class CratePanel {

		private final int keySlot = Crate.this.getInventorySize() - 5;
		private final Player player;

		private int rewardSlot = -1;

		private StandardPanel panel;

		private CratePanel(Player player)
		{
			this.player = player;
		}

		private void open()
		{
			this.panel = Panel.createStandard(Crate.this.getInventorySize(), this.getInventoryName());

			Crate.this.rewards.stream().map(reward -> reward.getValue().getItem()).forEach(panel::addItem);

			ItemStack key = ItemStack.builder().setType(Material.of("TRIPWIRE_HOOK")).build();

			ItemMeta meta = key.getMeta();

			meta.setDisplayName(Crate.this.getName() + " Crate");

			meta.setAllLore(
					Arrays.asList(
							"",
							ChatUtils.color(this.player.getLocalizedMessage("crates-click-to-open")),
							"",
							ChatUtils.color(this.player.getLocalizedMessage("crates-item-keys", Crate.this.getKeys(player)))
					)
			);

			key.setMeta(meta);

			panel.setItem(this.keySlot, key);

			panel.withClickConsumer(event ->
			{
				int slot = event.getSlot();

				if (slot != this.keySlot)
				{
					return;
				}

				int keys = Crate.this.getKeys(this.player);

				if (keys < 1)
				{
					this.player.closeInventory();

					this.player.sendLocalizedMessage("crate-no-keys", Crate.this.getName());

					return;
				}

				CrateOpenEvent call = new CrateOpenEvent(this.player, Crate.this);

				call.fire();

				if (call.isCancelled())
				{
					return;
				}

				Crate.this.removeKeys(this.player, 1);

				IconnedReward reward = RandomUtils.randomValue(Crate.this.rewards, Crate.this.totalWeight);

				this.rewardSlot = -1;

				int currentSlot = 0;

				for (Reward val : Crate.this.rewards.stream().map(rew -> rew.getValue().getReward()).collect(Collectors.toList()))
				{
					if (!reward.getReward().equals(val))
					{
						currentSlot++;

						continue;
					}

					this.rewardSlot = currentSlot;

					break;
				}

				if (this.rewardSlot == -1)
				{
					return;
				}

				new Open(this.player, this, reward.getReward(), this.rewardSlot);
			});

			panel.open(this.player);
		}

		private String getInventoryName()
		{
			return ChatUtils.color("&6" + Crate.this.getName() + " Crate");
		}

		public StandardPanel getPanel()
		{
			return panel;
		}

	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Crate> {

		private String name;
		private List<Weighted<IconnedReward>> rewards = Lists.newArrayList();
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

		public Builder withReward(IconnedReward reward, int weight)
		{
			Weighted<IconnedReward> weightedReward = Weighted.<IconnedReward>builder().setValue(reward).setWeight(weight).build();

			this.rewards.add(weightedReward);

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
