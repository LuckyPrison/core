package com.ulfric.core.homes;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandSethome extends Command {

	private final ModuleHomes base;

	public CommandSethome(ModuleHomes owner)
	{
		super("sethome", owner);

		this.base = owner;

		this.addEnforcer(Enforcers.IS_PLAYER, "home.must_be_player");

		this.addOptionalArgument(Argument.builder().addResolver(Resolvers.STRING).setPath("home-name").build());
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();

		String name = (String) this.getObj("home-name").orElse("home");

		if (!StringUtils.isAlphanumeric(name))
		{
			sender.sendLocalizedMessage("home-must-be-alphanumeric");

			return;
		}

		List<Home> homes = this.base.getHomes(sender);

		if (homes.size() >= sender.getLimit("homes").toInt())
		{

			boolean contained = false;

			for (Home home : homes)
			{
				if (home.getName().equalsIgnoreCase(name))
				{
					contained = true;

					this.base.delete(home);

					break;
				}
			}

			if (!contained)
			{
				sender.sendLocalizedMessage("home.max-homes");

				return;
			}
		}

		Home home = Home.builder().setOwner(sender).setLocation(sender.getLocation()).setName(name).build();

		this.base.save(home);

		sender.sendLocalizedMessage("home.set_home", home.getName());
	}

}
