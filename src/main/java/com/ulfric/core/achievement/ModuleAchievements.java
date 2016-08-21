package com.ulfric.core.achievement;

import com.ulfric.lib.coffee.module.Module;

public class ModuleAchievements extends Module {

	public ModuleAchievements()
	{
		super("achievements", "/achievements or /goals", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandAchievements(this));
	}

}