package com.ulfric.core.crate;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
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
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.location.LocationUtils;

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

		document.getDocument("crates").getKeys().forEach(key ->
		{
			MutableDocument section = document.getDocument("crates." + key);

			Crate.Builder builder = Crate.builder();

			builder	.withId(section.getInteger("id"))
					.withName(section.getString("name"));

			section.getStringList("locations").stream().map(LocationUtils::fromString).forEach(builder::withLocation);

			builder.withReward(Rewards.parseMultiReward(section.getDocument("rewards")));

			this.crates.add(builder.build());
		});
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
