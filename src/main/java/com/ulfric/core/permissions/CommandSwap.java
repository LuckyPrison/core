package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Entity;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Permissible;
import com.ulfric.lib.coffee.npermission.Permissions;

final class CommandSwap extends Command {

	public CommandSwap(ModuleBase owner)
	{
		super("swap", owner);

		this.addArgument(Argument.builder().addSimpleResolver(Permissions::getGroup).setPath("old-group").setUsage("permissions-specify-old-group").build());
		this.addArgument(Argument.builder().addSimpleResolver(Permissions::getGroup).setPath("new-group").setUsage("permissions-specify-new-group").build());
	}

	@Override
	public void run()
	{
		Permissible permissible = (Permissible) this.getObject("permissible");
		Group oldGroup = (Group) this.getObject("old-group");
		Group newGroup = (Group) this.getObject("new-group");

		if (oldGroup == newGroup) return;

		permissible.swapGroups(oldGroup, newGroup);

		if (!(permissible instanceof Entity)) return;

		Permissions.saveEntity((Entity) permissible);
	}

}