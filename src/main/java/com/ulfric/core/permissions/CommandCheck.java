package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Entity;
import com.ulfric.lib.coffee.npermission.Permissible;
import com.ulfric.lib.coffee.npermission.Permissions;

final class CommandCheck extends Command {

	public CommandCheck(ModuleBase owner, Argument addable)
	{
		super("add", owner);

		this.addArgument(addable);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Permissible permissible = (Permissible) this.getObject("permissible");
		Addable addable = (Addable) this.getObject("addable");

		if (addable.add(permissible))
		{
			sender.sendLocalizedMessage("permissions-add-success", permissible, addable);

			return;
		}

		sender.sendLocalizedMessage("permissions-add-failure", permissible, addable);

		if (!(permissible instanceof Entity)) return;

		Permissions.saveEntity((Entity) permissible);
	}

}