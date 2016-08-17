package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class CommandPing extends Command {

	public CommandPing(ModuleBase owner)
	{
		super("ping", owner, "pings");

		this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setDefaultValue(cmd ->
		{
			CommandSender sender = cmd.getSender();

			if (!(sender instanceof Player)) return null;

			return sender;
		}).setPermission("ping.others").build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Player player = (Player) this.getObject("player");

		if (sender == player)
		{
			sender.sendLocalizedMessage("ping.self", player.getPing());

			return;
		}

		sender.sendLocalizedMessage("ping.other", player.getName(), player.getPing());
	}

}