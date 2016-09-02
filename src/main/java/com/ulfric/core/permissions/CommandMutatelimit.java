package com.ulfric.core.permissions;

import com.ulfric.core.permissions.Addable.LimitAddable;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Permissible;

final class CommandMutatelimit extends BaseCommand {

	CommandMutatelimit(ModuleBase owner)
	{
		super("limit", owner);

		this.addArgument(Argument.builder().addSimpleResolver(str ->
		{
			Permissible permissible = (Permissible) this.getObject("permissible");

			LimitAddable limit = LimitAddable.valueOf(permissible, str);

			if (limit == null) return null;

			return Addable.valueOf(limit);
		}).setPath("addable").setUsage("permissions.specify_limit").build());

		this.addCommand(new CommandAdd(owner));
		this.addCommand(new CommandRemove(owner));
	}

}