package com.ulfric.core.permissions;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import com.ulfric.core.permissions.Addable.LimitAddable;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Permissible;

final class CommandMutatelimit extends BaseCommand {

	CommandMutatelimit(ModuleBase owner)
	{
		super("limit", owner);

		// HACKY STUFF BRUH

		Mutable<Command> add = new MutableObject<>();
		Argument addArg = this.newArg(add);
		Command addCommand = new CommandAdd(owner, addArg);
		add.setValue(addCommand);
		this.addCommand(addCommand);

		Mutable<Command> remove = new MutableObject<>();
		Argument removeArg = this.newArg(remove);
		Command removeCommand = new CommandRemove(owner, removeArg);
		remove.setValue(removeCommand);
		this.addCommand(removeCommand);

		Mutable<Command> observe = new MutableObject<>();
		Argument observeArg = this.newArg(observe);
		Command observeCommand = new CommandRemove(owner, observeArg);
		observe.setValue(observeCommand);
		this.addCommand(observeCommand);
	}

	private Argument newArg(Mutable<Command> mutable)
	{
		return Argument.builder().addSimpleResolver(str ->
		{
			Permissible permissible = (Permissible) mutable.getValue().getObject("permissible");

			LimitAddable limit = LimitAddable.valueOf(permissible, str);

			if (limit == null) return null;

			return Addable.valueOf(limit);
		}).setPath("addable").setUsage("permissions-specify-limit").build();
	}

}