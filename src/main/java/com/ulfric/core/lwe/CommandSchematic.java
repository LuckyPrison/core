package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.module.ModuleBase;

final class CommandSchematic extends BaseCommand {

	public CommandSchematic(ModuleBase owner)
	{
		super("schematic", owner, "schem");

		Command command = new CommandSchematicPaste(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));
	}

}