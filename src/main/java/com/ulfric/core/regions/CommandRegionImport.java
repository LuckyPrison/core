package com.ulfric.core.regions;

import com.ulfric.config.ConfigFile;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.region.Region;
import com.ulfric.lib.craft.region.RegionColl;

final class CommandRegionImport extends Command {

	CommandRegionImport(ModuleBase owner)
	{
		super("import", owner);

		this.addArgument(Argument.builder().setPath("file").addSimpleResolver(owner::getModuleConfig).build());
	}

	@Override
	public void run()
	{
		ConfigFile file = (ConfigFile) this.getObject("file");

		Region region = Region.fromDocument(file, file.getRoot());

		if (region == null) return;

		RegionColl.registerRegion(region);
	}

}