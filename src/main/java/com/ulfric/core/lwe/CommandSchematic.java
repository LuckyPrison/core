package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;

final class CommandSchematic extends BaseCommand {

	public CommandSchematic(ModuleBase owner)
	{
		super("schematic", owner, "schem");

		this.addCommand(new CommandSchematicPaste(owner));
	}

}