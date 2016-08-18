package com.ulfric.core.regions;
import com.ulfric.lib.coffee.module.Module;

public class ModuleRegionInterface extends Module {

	public ModuleRegionInterface()
	{
		super("region-interface", "Regions interface", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandRegions(this));
	}

}