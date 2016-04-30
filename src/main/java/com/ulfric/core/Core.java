package com.ulfric.core;

import com.ulfric.core.modules.ModuleTrash;
import com.ulfric.core.modules.ModuleWelcome;
import com.ulfric.core.scoreboard.ModuleScoreboardImplementation;
import com.ulfric.lib.bukkit.module.Plugin;

public class Core extends Plugin {

	@Override
	public void onFirstEnable()
	{
		this.addModule(new ModuleWelcome());
		this.addModule(new ModuleScoreboardImplementation());
		this.addModule(new ModuleTrash());
	}

}