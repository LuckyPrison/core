package com.ulfric.core.crate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.core.reward.Rewards;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent;
import com.ulfric.lib.craft.inventory.item.ItemParts;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.LocationUtils;
import com.ulfric.lib.craft.world.WorldUtils;

public final class ModuleCrates extends Module {

	public static final ModuleCrates INSTANCE = new ModuleCrates();

	private MultiSubscription<UUID, Document> subscription;
	private final List<Crate> crates = Lists.newArrayList();

	private ModuleCrates()
	{
		super("crates", "Crates / commands", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.loadSubscription();

		this.loadCrates();

		this.addCommand(new CommandKeys());
		this.addCommand(new CommandGivekey());

		this.addListener(new Listener(this) {

			@Handler
			public void on(PlayerInteractEvent event)
			{
				if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
				{
					return;
				}

				Block block = event.getBlock();

				if (block == null || block.getType() != Material.of("CHEST"))
				{
					return;
				}

				for (Crate crate : ModuleCrates.this.crates)
				{
					if (crate.atLocation(block.getLocation()))
					{
						crate.open(event.getPlayer());

						event.setCancelled(true);

						break;
					}
				}
			}

		});
	}

	private void loadSubscription()
	{
		DocumentStore database = PlayerUtils.getPlayerData();

		DataManager.get().ensureTableCreated(database, "crates");

		this.subscription = database.multi(
				Document.class, PlayerScopes.ONLINE, new DataAddress<>("crates", "data")
		).blockOnSubscribe(true).blockOnUnsubscribe(true).subscribe();
	}

	private void loadCrates()
	{
		MutableDocument document = this.getModuleConfig().getRoot();

		if (document.getDocument("crates") == null)
		{
			saveDefault();
		}

		document.getDocument("crates").getKeys().forEach(key ->
		{
			MutableDocument section = document.getDocument("crates." + key);

			Crate.Builder builder = Crate.builder();

			builder	.withId(section.getInteger("id"))
					.withName(section.getString("name"));

			section.getStringList("locations").stream().map(LocationUtils::fromString).forEach(builder::withLocation);

			section.getDocument("rewards").getKeys().forEach(rewardKey ->
			{
				Document rewardSection = section.getDocument("rewards." + rewardKey);

				builder.withReward(new IconnedReward(
						Rewards.parseReward(rewardSection.getDocument("reward")),
						ItemParts.stringToItem(rewardSection.getString("icon"))
				), rewardSection.getInteger("weight"));
			});

			this.crates.add(builder.build());
		});
	}

	private void saveDefault()
	{
		ConfigFile config = this.getModuleConfig();

		MutableDocument document = config.getRoot();

		document.set("crates.0.id", 0);
		document.set("crates.0.name", "DefaultCrate");
		document.set("crates.0.locations",
				Collections.singletonList(
						LocationUtils.toString(LocationUtils.getLocationAt(WorldUtils.getWorlds().get(0), 0, 100, 0))
				)
		);
		document.set("crates.0.rewards.0.icon", ItemParts.itemToString(ItemStack.builder().setType(Material.of("DIRT")).build()));
		document.set("crates.0.rewards.0.weight", 10);
		document.set("crates.0.rewards.0.reward.type", "money");
		document.set("crates.0.rewards.0.reward.amount", CurrencyAmount.of(Currency.getDefaultCurrency(), 100L).toString());

		document.set("crates.0.rewards.1.icon", ItemParts.itemToString(ItemStack.builder().setType(Material.of("STONE")).build()));
		document.set("crates.0.rewards.1.weight", 20);
		document.set("crates.0.rewards.1.reward.type", "money");
		document.set("crates.0.rewards.1.reward.amount", CurrencyAmount.of(Currency.getDefaultCurrency(), 200L).toString());

		document.set("crates.0.rewards.2.icon", ItemParts.itemToString(ItemStack.builder().setType(Material.of("APPLE")).build()));
		document.set("crates.0.rewards.2.weight", 30);
		document.set("crates.0.rewards.2.reward.type", "money");
		document.set("crates.0.rewards.2.reward.amount", CurrencyAmount.of(Currency.getDefaultCurrency(), 300L).toString());

		document.set("crates.0.rewards.3.icon", ItemParts.itemToString(ItemStack.builder().setType(Material.of("DIAMOND")).build()));
		document.set("crates.0.rewards.3.weight", 20);
		document.set("crates.0.rewards.3.reward.type", "money");
		document.set("crates.0.rewards.3.reward.amount", CurrencyAmount.of(Currency.getDefaultCurrency(), 400L).toString());

		document.set("crates.0.rewards.4.icon", ItemParts.itemToString(ItemStack.builder().setType(Material.of("GOLD_INGOT/")).build()));
		document.set("crates.0.rewards.4.weight", 10);
		document.set("crates.0.rewards.4.reward.type", "money");
		document.set("crates.0.rewards.4.reward.amount", CurrencyAmount.of(Currency.getDefaultCurrency(), 500L).toString());

		config.save();
	}

	public MultiSubscription<UUID, Document> getSubscription()
	{
		return subscription;
	}

	public List<Crate> getCrates()
	{
		return this.crates;
	}

	private final class CommandKeys extends Command {

		CommandKeys()
		{
			super("keys", ModuleCrates.this);

			this.addEnforcer(Enforcers.IS_PLAYER, "crates-is-not-player");

			this.addOptionalArgument(OfflinePlayer.ARGUMENT);
		}

		@Override
		public void run()
		{
			Player sender = (Player) this.getSender();

			StringBuilder builder = new StringBuilder();
			builder.append(sender.getLocalizedMessage("crate-keys"));

			OfflinePlayer target = ((OfflinePlayer) this.getObj(OfflinePlayer.ARGUMENT.getPath()).orElse(sender));

			for (Crate crate : ModuleCrates.this.crates)
			{
				String name = crate.getName();

				int amount = crate.getKeys(target);

				builder.append(sender.getLocalizedMessage("crate-key-list", name, amount));
			}

			this.getSender().sendMessage(builder.toString());
		}

	}

	private final class CommandGivekey extends Command {

		CommandGivekey()
		{
			super("givekey", ModuleCrates.this);

			this.addArgument(OfflinePlayer.ARGUMENT);
			this.addArgument(Crate.ARGUMENT);
			this.addOptionalArgument(Argument.builder().addResolver(Resolvers.INTEGER).setPath("amount").setDefaultValue(1).build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			if (!sender.hasPermission("crates.givekey"))
			{
				sender.sendLocalizedMessage("crates-no-permission");

				return;
			}

			OfflinePlayer target = (OfflinePlayer) this.getObject("offline-player");

			Crate crate = (Crate) this.getObject("crate");

			int amount = (int) this.getObj("amount").orElse(1);

			if (target == null || crate == null || amount < 1)
			{
				sender.sendLocalizedMessage("crates-invalid-usage");

				return;
			}

			crate.giveKeys(target, amount);

			sender.sendLocalizedMessage("crate-gave-keys", target.getName(), amount);
		}

	}

}
