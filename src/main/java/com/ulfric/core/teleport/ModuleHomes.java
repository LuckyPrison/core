package com.ulfric.core.teleport;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.command.ArgFunction;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.function.SortUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.SearchResult;
import com.ulfric.lib.coffee.string.StringUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public final class ModuleHomes extends Module {

	public ModuleHomes()
	{
		super("homes", "Sethome/Home module", "1.0.0", "Packet");
	}

	Map<UUID, Set<Warp>> homes;
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
		this.subscription.unsubscribe();

		// TODO serialize homes
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

				Set<Warp> homesList = Sets.newTreeSet();

				for (String key : value.getKeys("homes", false))
				{
					Document homeToParse = value.getDocument(key);

					homesList.add(Warp.fromDocument(key, homeToParse));
				}

				this.homes.put(newValue.getAddress().getId(), homesList);
			})
			.subscribe();

		this.addCommand(new Command("home", this)
		{
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

				Set<Warp> foundHomes = ModuleHomes.this.homes.get(uuid);

				if (SetUtils.isEmpty(foundHomes))
				{
					sender.sendLocalizedMessage("homes.no_homes");

					return;
				}

				Warp home;
				String homeName = (String) this.getObject("home");

				if (homeName != null)
				{
					SearchResult<String, Warp> result = StringUtils.getClosestNamed(foundHomes, homeName);
					home = result.getValue();

					if (home == null)
					{
						int size = foundHomes.size();

						if (size <= 1)
						{
							sender.sendLocalizedMessage("home.no_home_found", homeName);

							return;
						}

						sender.sendLocalizedMessage("home.no_home_found_multiple", size, homeName);

						return;
					}

					if (!homeName.equalsIgnoreCase(home.getName()))
					{
						sender.sendLocalizedMessage("home.found_closest", homeName, home.getName(), result.getDistance());
					}
				}
				else
				{
					home = SortUtils.getHighest(foundHomes);
				}

				sender.sendLocalizedMessage("home.teleporting", home.getName(), home.getDelay());

				home.accept((Player) sender);
			}
		}.addOptionalArgument(Argument.builder().setPath("home").addResolver(ArgFunction.STRING_FUNCTION).build())
		 .addOptionalArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setPermission("home.others").build()));

		// TODO sethome - Adam Edwards @ 8/2/2016
		/*this.addCommand(new Command("sethome", this, "shome", "sethme", "shme", "createhome", "makehome", "chome", "mhome", "newhome", "nhome")
		{
			@Override
			public void run()
			{
				String home = (String) this.getObject("name");
				Material material = (Material) this.getObject("material");

				
			}
		}.addArgument(Argument.builder().setPath("name").setDefaultValue("home").addResolver(ArgFunction.STRING_FUNCTION).setPermission("sethome.multiple").build())
		 .addArgument(Argument.builder().setPath("material").setDefaultValue(Material.of("GRASS")).addResolver((sen, str) -> Material.of(str)).setPermission("sethome.itemtype").build()));*/
	}

}