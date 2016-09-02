package com.ulfric.core.teleport;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

final class CommandTeleport extends Command {

	public CommandTeleport(ModuleBase owner)
	{
		super("teleport", owner, "tele", "tp");

		this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setUsage("teleport-specify-player").build());

		this.addPermission("moderator");

		this.addEnforcer(Enforcers.IS_PLAYER, "teleport-must-be-player");
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();
		Player player = (Player) this.getObject("player");

		if (player == sender) return;

		sender.teleport(player.getLocation());
	}

}