package com.ulfric.core.teleport;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.location.LocationUtils;

final class CommandTeleportPosition extends Command {

	public CommandTeleportPosition(ModuleBase owner)
	{
		super("teleportposition", owner, "teleposition", "telepos", "tppos");

		this.addArgument(Argument.builder().setPath("x").addResolver(Resolvers.DOUBLE).setUsage("teleport-position-specify-x").build());
		this.addArgument(Argument.builder().setPath("y").addResolver(Resolvers.DOUBLE).setUsage("teleport-position-specify-y").build());
		this.addArgument(Argument.builder().setPath("z").addResolver(Resolvers.DOUBLE).setUsage("teleport-position-specify-z").build());

		this.addPermission("moderator");

		this.addEnforcer(Enforcers.IS_PLAYER, "teleport-position-must-be-player");
	}

	@Override
	public void run()
	{
		double x = (double) this.getObject("x");
		double y = (double) this.getObject("y");
		double z = (double) this.getObject("z");

		Player player = (Player) this.getSender();

		player.teleportRelative(LocationUtils.getLocationAt(player.getWorld(), x, y, z));
	}

}