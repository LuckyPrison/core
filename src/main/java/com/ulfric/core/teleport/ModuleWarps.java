package com.ulfric.core.teleport;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DataContainer;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.ReferenceCountedScope;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.StringUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.Destination;
import com.ulfric.lib.craft.panel.Panel;

public final class ModuleWarps extends Module {

	public ModuleWarps()
	{
		super("warps", "Warping module", "1.0.0", "Packet");
	}

	Map<String, Warp> warps;
	private MultiSubscription<String, Document> subscription;
	private Panel panel;
	final Argument warpArgument = Argument.builder().setPath("warp").addResolver((sen, str) ->
	{
		Warp warp = ModuleWarps.this.warps.get(str);

		if (warp == null)
		{
			warp = ModuleWarps.this.warps.get(StringUtils.getClosest(ModuleWarps.this.warps.keySet(), str, 3).getValue());
		}

		if (warp == null) return null;

		if (!sen.hasPermission("warps." + warp.getName())) return null;

		return warp;
	}).build();

	@Override
	public void onFirstEnable()
	{
		this.warps = new CaseInsensitiveMap<>();

		DocumentStore store = DataManager.get().getEnsuredDatabase("warps");

		this.subscription = store.multi(Document.class, new ReferenceCountedScope<String>(), new DataAddress<>("warps", null, null))
								 .blockOnSubscribe(true)
								 .onChange((oldValue, newValue) ->
								 {
									 String key = newValue.getAddress().getId();
									 this.warps.put(key, Warp.fromDocument(key, newValue.getValue()));
								 })
								 .subscribe();

		this.addCommand(new CommandWarp());
		this.addCommand(new CommandWarps());
		this.addCommand(new CommandSetWarp());
		this.addCommand(new CommandWarpOpen());
		this.addCommand(new CommandWarpClose());
	}

	void displayWarpsMenu(CommandSender sender)
	{
		if (!(sender instanceof Player)) return;

		this.panel.open((Player) sender);
	}

	@Override
	public void onModuleEnable()
	{
		this.panel = Panel.createStandard(this.getModuleConfig("warp-menu").getRoot());

		if (!this.subscription.isSubscribed())

		this.subscription.subscribe();
	}

	@Override
	public void onModuleDisable()
	{
		this.panel = null;

		this.subscription.unsubscribe(); // TODO before or after??

		for (Entry<String, Warp> entry : this.warps.entrySet())
		{
			Warp warp = entry.getValue();

			DataContainer<String, Document> container = this.subscription.get(entry.getKey());

			MutableDocument document = new SimpleDocument();

			document.set("item", warp.itemToString());
			document.set("visits", warp.getVisits());

			MutableDocument destinationDocument = document.createDocument("destination");

			destinationDocument.set("location", warp.locationToString());
			destinationDocument.set("delay", warp.getDelay());
			destinationDocument.set("closed", warp.isClosed());

			container.setValue(document);
		}
	}

	final class CommandWarps extends Command
	{
		public CommandWarps()
		{
			super("warps", ModuleWarps.this, "warpslist", "warplist");
		}

		@Override
		public void run()
		{
			ModuleWarps.this.displayWarpsMenu(this.getSender());
		}
	}

	final class CommandSetWarp extends Command
	{
		public CommandSetWarp()
		{
			super("setwarp", ModuleWarps.this, "swarp", "createwarp", "cwarp", "makewarp", "mwarp");

			this.addPermission("setwarp.use");

			this.addArgument(Argument.builder().setPath("warp").addResolver(Resolvers.STRING).build());
			this.addOptionalArgument(Argument.builder().setPath("override").addResolver((sen, str) -> str.toLowerCase().equals("-o")).build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			if (!(sender instanceof Player))
			{
				sender.sendLocalizedMessage("setwarp.must_be_player");

				return;
			}

			String warpName = (String) this.getObject("warp");

			Warp existing = ModuleWarps.this.warps.get(warpName);

			if (existing != null)
			{
				if (!this.hasObject("override"))
				{
					sender.sendLocalizedMessage("setwarp.warp_exists", warpName);

					return;
				}
			}

			Warp warp = Warp.newWarp(warpName, Destination.newDestination(((Player) sender).getLocation(), 5), Material.of("GRASS").toItem());

			ModuleWarps.this.warps.put(warpName, warp);
		}
	}

	final class CommandWarp extends Command
	{
		public CommandWarp()
		{
			super("warp", ModuleWarps.this, "warpto", "goto");

			this.addOptionalArgument(ModuleWarps.this.warpArgument);
			this.addOptionalArgument(Argument.builder().setPath("player").setPermission("warp.others").addResolver(PlayerUtils::getOnlinePlayer).build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			Warp warp = (Warp) this.getObject("warp");

			if (warp == null)
			{
				ModuleWarps.this.displayWarpsMenu(sender);

				return;
			}

			if (warp.isClosed())
			{
				if (!sender.hasPermission("warp.closed"))
				{
					sender.sendLocalizedMessage("warp.closed", warp.getName());

					return;
				}

				sender.sendLocalizedMessage("warp.closed_bypass", warp.getName());
			}

			Player player = (Player) this.getObject("player");

			if (player == null)
			{
				if (!(sender instanceof Player))
				{
					sender.sendLocalizedMessage("warp.specify_player");

					return;
				}

				player = (Player) sender;
			}
			else if (!player.getUniqueId().equals(sender.getUniqueId()))
			{
				sender.sendLocalizedMessage("warp.warping_player", player.getName(), warp.getName());
			}

			player.sendLocalizedMessage("warp.warping", warp.getName(), warp.getDelay());

			if (RandomUtils.percentChance(0.20))
			{
				player.sendLocalizedMessage("warp.total_visits", warp.getName(), warp.getVisits());
			}

			warp.accept(player);
		}
	}

	final class CommandWarpOpen extends Command
	{
		public CommandWarpOpen()
		{
			super("warpopen", ModuleWarps.this, "openwarp", "owarp", "warpo");

			this.addArgument(ModuleWarps.this.warpArgument);

			this.addPermission("warp.open");
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();
			Warp warp = (Warp) this.getObject("warp");

			if (warp.isNotClosed())
			{
				sender.sendLocalizedMessage("warp.already_open", warp.getName());

				return;
			}

			warp.setClosed(false);

			sender.sendLocalizedMessage("warp.opened", warp.getName());
		}
	}

	final class CommandWarpClose extends Command
	{
		public CommandWarpClose()
		{
			super("warpclose", ModuleWarps.this, "closewarp", "cwarp", "warpc");

			this.addArgument(ModuleWarps.this.warpArgument);

			this.addPermission("warp.close");
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();
			Warp warp = (Warp) this.getObject("warp");

			if (warp.isClosed())
			{
				sender.sendLocalizedMessage("warp.already_closed", warp.getName());

				return;
			}

			warp.setClosed(true);

			sender.sendLocalizedMessage("warp.closed", warp.getName());
		}
	}

}