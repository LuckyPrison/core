package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;

final class CommandPermissions extends BaseCommand {

	CommandPermissions(ModuleBase owner)
	{
		super("permissions", owner, "permission", "perm");

		this.addCommand(new CommandUser(owner));
		this.addCommand(new CommandGroup(owner));
		/* 
		 * 
		 * perm user Packet perm add sethome.use
		 * perm user Packet limit add homes:+10
		 * 
		 */
	}

}