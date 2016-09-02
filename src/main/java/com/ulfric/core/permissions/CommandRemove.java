package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Entity;
import com.ulfric.lib.coffee.npermission.Permissible;
import com.ulfric.lib.coffee.npermission.Permissions;

final class CommandRemove extends Command {

	public CommandRemove(ModuleBase owner)
	{
		super("remove", owner, "delete", "del");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Permissible permissible = (Permissible) this.getObject("permissible");
		Addable addable = (Addable) this.getObject("addable");

		if (addable.remove(permissible))
		{
			sender.sendLocalizedMessage("permissions-remove-success", permissible, addable);

			return;
		}

		sender.sendLocalizedMessage("permissions-remove-failure", permissible, addable);

		if (!(permissible instanceof Entity)) return;

		Permissions.saveEntity((Entity) permissible);
	}

}