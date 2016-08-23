package com.ulfric.core.backpack;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

import java.util.Map;
import java.util.UUID;

public final class ModuleBackpack extends Module {
	// TODO: Resolve
	private static final Argument PAGE = Argument.builder().addResolver(null).setPath("page").setDefaultValue(1).build();
	// TODO: Resolve
	private static final Argument PLAYER = Argument.builder().addResolver(null).setPath("player").setDefaultValue(Command::getSender).setPermission("core.backpack.others").build();

	private MultiSubscription<UUID, Document> subscription;
	private final Map<UUID, Backpack> cache = Maps.newHashMap();

	public ModuleBackpack()
	{
		super("backpack", "Backpack command module", "1.0.0", "feildmaster");
	}

	@Override
	public void onFirstEnable()
	{
		DocumentStore database = PlayerUtils.getPlayerData();

		this.subscription = database
				.multi(Document.class, PlayerScopes.ONLINE, new DataAddress<>("backpacks", null, "contents"))
				.blockOnSubscribe(true)
				.subscribe();

		this.addCommand(new BackpackCommand().addPermission("core.backpack").addEnforcer(sender -> sender instanceof Player, "system.cmd_player_only").addArgument(PAGE).addArgument(PLAYER));
	}

	@Override
	public void onModuleDisable()
	{
		this.subscription.unsubscribe();
	}

	class BackpackCommand extends Command {

		BackpackCommand()
		{
			super("backpack", ModuleBackpack.this, "pack");
		}

		@Override
		public void run()
		{
			Player sender = (Player) this.getSender();
			int page = Math.max((int) this.getObject("page"), 1);
			OfflinePlayer target = (OfflinePlayer) this.getObject("player");

			// TODO: Get max backpacks of "target"
			int max = 1;
			// Check if page is < max backpacks
			if (page > max)
			{
				sender.sendLocalizedMessage("core.backpack.limit", max);
				return;
			}
			// Display backpack
			new Backpack(sender, target, page, max).open(sender);
		}
	}

	protected void save(Backpack backpack)
	{
		MutableDocument document = new SimpleDocument();

		backpack.into(document);

		this.subscription.get(backpack.getPlayer().getUniqueId()).setValue(document);
	}

	protected Backpack get(Player player)
	{
		Backpack cached = this.cache.get(player.getUniqueId());

		if (cached != null)
		{
			return cached;
		}

		Document document = this.subscription.get(player.getUniqueId()).getValue();

		if (document == null || document.getKeys().size() == 0)
		{
			return makeBackpack(player);
		}

		return Backpack.fromDocument(player, document);
	}

	private Backpack makeBackpack(Player player)
	{
		Backpack backpack = new Backpack(player, player, 1, 1);

		this.save(backpack);

		return backpack;
	}

}
