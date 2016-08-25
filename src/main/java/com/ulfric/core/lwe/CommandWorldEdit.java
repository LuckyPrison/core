package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;

final class CommandWorldEdit extends BaseCommand {

	CommandWorldEdit(ModuleBase owner)
	{
		super("worldedit", owner, "we");

		this.addEnforcer(Enforcers.IS_PLAYER, "worldedit.must_be_player");

		this.addPermission("worldedit.use");

		Command command = new CommandSet(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new CommandDelete(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new CommandSelection(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("select").add("sel").build());

		command = new CommandSchematic(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("schem").build());
	}

}