package com.ulfric.core.regions;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;

final class CommandRegions extends Command {

	public CommandRegions(ModuleBase owner)
	{
		super("regions", owner, "region", "rg", "guards", "guard", "gu");

		this.addPermission("regions.use");
	}

	@Override
	public void run()
	{
		// TODO show sub commands
	}

}