package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;

final class CommandWorldEdit extends BaseCommand {

	CommandWorldEdit(ModuleBase owner)
	{
		super("worldedit", owner, "we");

		this.addEnforcer(Enforcers.IS_PLAYER, "worldedit-must-be-player");

		this.addPermission("worldedit.use");

		this.addCommand(new CommandSet(owner));
		this.addCommand(new CommandDelete(owner));
		this.addCommand(new CommandSelection(owner));
		this.addCommand(new CommandSchematic(owner));
	}

}