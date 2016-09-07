package com.ulfric.core.homes;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandHome extends Command {

	private final ModuleHomes base;

	public CommandHome(ModuleHomes owner)
	{
		super("home", owner);

		this.base = owner;

		this.addEnforcer(Enforcers.IS_PLAYER, "home.must_be_player");

		this.addOptionalArgument(OfflinePlayer.ARGUMENT);
		this.addOptionalArgument(Argument.builder().addResolver(Resolvers.STRING).setPath("home-name").build());
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();

		Player target = (Player) this.getObj("offline-player").orElse(sender);

		String homeName = (String) this.getObj("home-name").orElseGet(() ->
		{
			List<Home> homes = this.base.getHomes(target);
			if (homes.size() == 1)
			{
				return homes.get(0).getName();
			}
			return homes.stream().filter(home -> home.getName().equalsIgnoreCase("home")).findAny().isPresent() ? "home" : null;
		});

		if (StringUtils.isBlank(homeName))
		{
			sender.sendLocalizedMessage("home.specify_home");

			return;
		}

		Home home = this.base.getHome(target, homeName);

		if (home == null)
		{
			sender.sendLocalizedMessage("home.home_not_found");

			return;
		}

		if (!home.canTeleport(sender))
		{
			sender.sendLocalizedMessage("home.no_permission");

			return;
		}

		sender.sendLocalizedMessage("home.teleporting", homeName);

		home.teleportTo(sender);
	}

}
