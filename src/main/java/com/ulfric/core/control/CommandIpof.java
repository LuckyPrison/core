package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class CommandIpof extends Command {

	public CommandIpof(ModuleBase owner)
	{
		super("ipof", owner);

		this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setDefaultValue(cmd ->
		{
			CommandSender sender = cmd.getSender();

			if (!(sender instanceof Player)) return null;

			return sender;
		}).setUsage("ipof.specify_player").setPermission("ipof.others").build());

		this.addPermission("ipof.use");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Player player = (Player) this.getObject("player");

		if (sender == player)
		{
			sender.sendLocalizedMessage("ipof.self", player.getSimpleIP());

			return;
		}

		sender.sendLocalizedMessage("ipof.other", player.getName(), player.getSimpleIP());
	}

}