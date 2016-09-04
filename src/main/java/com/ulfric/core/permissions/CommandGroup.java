package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Permissions;

final class CommandGroup extends BaseCommand {

	CommandGroup(ModuleBase owner)
	{
		super("group", owner);

		this.addArgument(Argument.builder().addSimpleResolver(str ->
		{
			Group group = Permissions.getGroup(str);

			if (group == null) return null;

			return group;
		}).setPath("permissible").setUsage("permissions-specify-group").build());

		this.addCommand(new CommandMutateperm(owner));
		this.addCommand(new CommandMutategroup(owner));
		this.addCommand(new CommandMutatelimit(owner));
	}

}