package com.ulfric.core.teleport;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DataContainer;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.command.ArgFunction;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.function.SortUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.permission.Permissible;
import com.ulfric.lib.coffee.string.SearchResult;
import com.ulfric.lib.coffee.string.StringUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.Destination;

public final class ModuleHomes extends Module {

	public ModuleHomes()
	{
		super("homes", "Sethome/Home module", "1.0.0", "Packet");
	}

	Map<UUID, WarpSet> homes;
	private MultiSubscription<UUID, Document> subscription;

	@Override
	public void onModuleEnable()
	{
		if (this.subscription.isSusbcribed()) return;

		ThreadUtils.runAsync(this.subscription::subscribe);
	}

	@Override
	public void onModuleDisable()
	{
		this.subscription.unsubscribe(); // BEFORE OR AFTER?

		for (Entry<UUID, WarpSet> entry : this.homes.entrySet())
		{
			WarpSet warpSet = entry.getValue();

			if (!warpSet.hasBeenChanged()) continue;

			UUID uuid = entry.getKey();

			DataContainer<UUID, Document> container = ModuleHomes.this.subscription.get(uuid);

			MutableDocument document = new SimpleDocument();

			for (Warp warp : warpSet)
			{
				MutableDocument warpDoc = document.createDocument(warp.getName());

				warpDoc.set("item", warp.itemToString());
				warpDoc.set("visits", warp.getVisits());

				MutableDocument destinationDocument = warpDoc.createDocument("destination");

				destinationDocument.set("location", warp.locationToString());
				destinationDocument.set("delay", warp.getDelay());
			}

			container.setValue(document);
		}
	}

	@Override
	public void onFirstEnable()
	{
		this.homes = Maps.newConcurrentMap();

		DataManager.get()
			.getDatabase("homes")
			.multi(Document.class, PlayerScopes.ONLINE, new DataAddress<>("homes", null, null))
			.blockOnSubscribe(true)
			.onChange((oldValue, newValue) ->
			{
				Document value = newValue.getValue();

				WarpSet homesList = new WarpSet();

				for (String key : value.getKeys("homes", false))
				{
					Document homeToParse = value.getDocument(key);

					homesList.add(Warp.fromDocument(key, homeToParse));
				}

				this.homes.put(newValue.getAddress().getId(), homesList);
			})
			.subscribe();

		this.addCommand(new CommandHome());
		this.addCommand(new CommandDeleteHome());
		this.addCommand(new CommandSetHome());
	}

	Warp getHome(CommandSender sender, UUID uuid, String argument, boolean getHighest)
	{
		WarpSet foundHomes = ModuleHomes.this.homes.get(uuid);

		if (foundHomes == null || foundHomes.isEmpty())
		{
			sender.sendLocalizedMessage("home.no_homes");

			return null;
		}

		Warp home = null;

		if (argument != null)
		{
			SearchResult<String, Warp> result = StringUtils.getClosestNamed(foundHomes, argument);
			home = result.getValue();

			if (home == null)
			{
				int size = foundHomes.size();

				if (size <= 1)
				{
					sender.sendLocalizedMessage("home.no_home_found", argument);

					return null;
				}

				sender.sendLocalizedMessage("home.no_home_found_multiple", size, argument);

				return null;
			}

			if (!argument.equalsIgnoreCase(home.getName()))
			{
				sender.sendLocalizedMessage("home.found_closest", argument, home.getName(), result.getDistance());
			}
		}
		else if (getHighest)
		{
			home = SortUtils.getHighest(foundHomes);
		}

		return home;
	}

	final class CommandSetHome extends Command
	{
		public CommandSetHome()
		{
			super("sethome", ModuleHomes.this, "shome", "createhome", "chome", "makehome", "mhome");

			this.addArgument(Argument.builder().setPath("home").setDefaultValue("home").addResolver(ArgFunction.STRING_FUNCTION).build());
			this.addArgument(Argument.builder().setPath("material").setDefaultValue(Material.of("GRASS")).addResolver((sen, str) ->
			{
				Material material = Material.of(str);

				if (Material.isNotBlock(material)) return null;

				return material;
			}).setPermission("sethome.itemtype").build());
			this.addOptionalArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setPermission("sethome.others").build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			if (!(sender instanceof Player))
			{
				sender.sendLocalizedMessage("sethome.must_be_player");

				return;
			}

			Player player = (Player) this.getObject("player");

			UUID uuid = (player == null ? sender : player).getUniqueId();

			if (uuid == null)
			{
				sender.sendLocalizedMessage("sethome.specify_player");

				return;
			}

			String name = (String) this.getObject("home");
			String lowerName = name.toLowerCase();
			Material material = (Material) this.getObject("material");

			WarpSet warps = ModuleHomes.this.homes.get(uuid);

			int visits = 0;

			if (warps == null)
			{
				warps = new WarpSet();

				ModuleHomes.this.homes.put(uuid, warps);
			}

			else
			{
				Permissible permissible = player == null ? sender : player;

				Iterator<Warp> iterator = warps.iterator();

				boolean found = false;

				while (iterator.hasNext())
				{
					Warp warp = iterator.next();

					if (!warp.getName().toLowerCase().equals(lowerName)) continue;

					warps.remove(warp);

					found = true;

					visits = warp.getVisits();

					break;
				}

				if (!found)
				{
					int limit = permissible.getLimit("homes");

					if (limit <= warps.size())
					{
						if (permissible == sender || player == null)
						{
							sender.sendLocalizedMessage("sethome.limit_reached", limit);

							return;
						}

						sender.sendLocalizedMessage("sethome.limit_reached_other", player.getName(), limit);

						return;
					}
				}
			}

			Warp warp = Warp.newWarp(lowerName, Destination.newDestination(((Player) sender).getLocation(), 5), material.toItem(), visits);

			warps.add(warp);
		}
	}

	final class CommandDeleteHome extends Command
	{
		public CommandDeleteHome()
		{
			super("deletehome", ModuleHomes.this, "delhome", "dhome");

			this.addArgument(Argument.builder().setPath("home").setDefaultValue("home").addResolver(ArgFunction.STRING_FUNCTION).build());
			this.addOptionalArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setPermission("delhome.others").build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			if (!(sender instanceof Player)) return;

			UUID uuid;
			Player fetch = (Player) this.getObject("player");

			if (fetch == null)
			{
				uuid = sender.getUniqueId();
			}
			else
			{
				uuid = fetch.getUniqueId();
			}

			Warp home = ModuleHomes.this.getHome(sender, uuid, (String) this.getObject("home"), false);

			if (home == null) return;

			if (!ModuleHomes.this.homes.get(uuid).remove(home))
			{
				sender.sendLocalizedMessage("delhome.error", home.getName());

				return;
			}

			if (fetch == null)
			{
				sender.sendLocalizedMessage("delhome.success", home.getName());

				return;
			}

			sender.sendLocalizedMessage("delhome.success_other", home.getName(), fetch.getName());
		}
	}

	final class CommandHome extends Command
	{
		public CommandHome()
		{
			super("home", ModuleHomes.this);

			this.addOptionalArgument(Argument.builder().setPath("home").addResolver(ArgFunction.STRING_FUNCTION).build());
			this.addOptionalArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setPermission("home.others").build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			if (!(sender instanceof Player)) return;

			UUID uuid;
			Player fetch = (Player) this.getObject("player");

			if (fetch == null)
			{
				uuid = sender.getUniqueId();
			}
			else
			{
				uuid = fetch.getUniqueId();
			}

			Warp home = ModuleHomes.this.getHome(sender, uuid, (String) this.getObject("home"), true);

			if (home == null) return;

			sender.sendLocalizedMessage("home.teleporting", home.getName(), home.getDelay());

			home.accept((Player) sender);
		}
	}

}