package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Permissions;

final class CommandMutategroup extends BaseCommand {

	CommandMutategroup(ModuleBase owner)
	{
		super("group", owner);

		Argument addable = Argument.builder().addSimpleResolver(str ->
		{
			Group group = Permissions.getGroup(str);

			if (group == null) return null;

			return Addable.valueOf(group);
		}).setPath("addable").setUsage("permissions-specify-group").build();

		this.addCommand(new CommandAdd(owner, addable));
		this.addCommand(new CommandRemove(owner, addable));
	}

}