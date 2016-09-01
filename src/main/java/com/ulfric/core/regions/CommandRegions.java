package com.ulfric.core.regions;

import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;

final class CommandRegions extends BaseCommand {

	CommandRegions(ModuleBase owner)
	{
		super("regions", owner, "region", "rg", "guards", "guard", "gu");

		this.addCommand(new CommandRegionImport(owner));
		this.addCommand(new CommandRegionCreate(owner));
		this.addCommand(new CommandFlag(owner));

		this.addPermission("regions.use");
	}

}