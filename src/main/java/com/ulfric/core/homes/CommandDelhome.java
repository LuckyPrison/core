package com.ulfric.core.homes;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandDelhome extends Command {

	private final ModuleHomes base;

	public CommandDelhome(ModuleHomes owner)
	{
		super("delhome", owner);

		this.base = owner;

		this.addEnforcer(Enforcers.IS_PLAYER, "home.must_be_player");

		this.addOptionalArgument(Argument.builder().addResolver(Resolvers.STRING).setPath("home-name").build());
	}

	@Override
	public void run()
	{
		Player sender = (Player) getSender();

		String name = (String) this.getObj("home-name").orElse(this.base.getDefault(sender));

		if (StringUtils.isBlank(name))
		{
			sender.sendLocalizedMessage("home.specify_home");

			return;
		}

		Home home = this.base.getHome(sender, name);

		if (home == null)
		{
			sender.sendLocalizedMessage("home.not_found", name);

			return;
		}

		this.base.delete(home);

		sender.sendLocalizedMessage("home.deleted", name);
	}
}
