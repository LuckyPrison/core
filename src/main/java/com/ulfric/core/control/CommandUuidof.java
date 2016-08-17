package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandUuidof extends Command {

	public CommandUuidof(ModuleBase owner)
	{
		super("uuidof", owner);

		this.addArgument(Argument.builder().setPath("player").addResolver(OfflinePlayer.ARGUMENT).setDefaultValue(cmd ->
		{
			CommandSender sender = cmd.getSender();

			if (!(sender instanceof Player)) return null;

			return sender;
		}).setUsage("uuidof.specify_player").setPermission("uuidof.others").build());

		this.addPermission("uuidof.use");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		if (sender == player)
		{
			sender.sendLocalizedMessage("uuid.self", player.getUniqueId());

			return;
		}

		sender.sendLocalizedMessage("uuid.other", player.getName(), player.getUniqueId());
	}

}