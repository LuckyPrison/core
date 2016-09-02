package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Permissions;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

final class CommandUser extends BaseCommand {

	CommandUser(ModuleBase owner)
	{
		super("user", owner);

		this.addArgument(Argument.builder().addSimpleResolver(str ->
		{
			OfflinePlayer player = PlayerUtils.getOfflinePlayer(str);

			if (player == null) return null;

			return Permissions.getEntity(player.getUniqueId());
		}).setPath("permissible").setUsage("permissions-specify-user").build());

		this.addCommand(new CommandMutateperm(owner));
		this.addCommand(new CommandMutategroup(owner));
		this.addCommand(new CommandMutatelimit(owner));
	}

}