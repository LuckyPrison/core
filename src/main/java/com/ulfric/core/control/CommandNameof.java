package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandNameof extends Command {

	public CommandNameof(ModuleBase owner)
	{
		super("nameof", owner);

		this.addArgument(Argument.builder().setPath("player").addResolver(OfflinePlayer.ARGUMENT).setDefaultValue(cmd ->
		{
			CommandSender sender = cmd.getSender();

			if (!(sender instanceof Player)) return null;

			return sender;
		}).setUsage("nameof.specify_player").setPermission("nameof.others").build());

		this.addPermission("punishment.nameof_use");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		if (sender == player)
		{
			sender.sendLocalizedMessage("punishment.nameof_self", player.getName());

			return;
		}

		sender.sendLocalizedMessage("punishment.nameof_other", player.getName());
	}

}