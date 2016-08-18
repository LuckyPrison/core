package com.ulfric.core.regions;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.module.ModuleBase;

final class CommandRegions extends Command {

	public CommandRegions(ModuleBase owner)
	{
		super("regions", owner, "region", "rg", "guards", "guard", "gu");

		Command command = new CommandRegionImport(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		this.addPermission("regions.use");
	}

	@Override
	public void run()
	{
		// TODO show sub commands
	}

}