package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Permission;
import com.ulfric.lib.coffee.npermission.Permissions;

final class CommandMutateperm extends BaseCommand {

	CommandMutateperm(ModuleBase owner)
	{
		super("permission", owner, "perm");

		Argument addable = Argument.builder().addSimpleResolver(str ->
		{
			Permission p = Permissions.getPermission(str);

			if (p == null) return null;

			return Addable.valueOf(p);
		}).setPath("addable").setUsage("permissions-specify-perm").build();

		this.addCommand(new CommandAdd(owner, addable));
		this.addCommand(new CommandRemove(owner, addable));
		this.addCommand(new CommandObserve(owner, addable));
	}

}