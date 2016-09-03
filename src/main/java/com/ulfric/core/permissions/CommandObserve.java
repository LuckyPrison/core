package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Permissible;

final class CommandObserve extends Command {

	public CommandObserve(ModuleBase owner, Argument addable)
	{
		super("observe", owner, "check", "view");

		this.addArgument(addable);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Permissible permissible = (Permissible) this.getObject("permissible");
		Addable addable = (Addable) this.getObject("addable");

		String observe = addable.observe(permissible);

		sender.sendLocalizedMessage("permissions-observe", permissible, addable, observe);
	}

}