package com.ulfric.core;

import com.ulfric.core.economy.ModuleEconomyInterface;
import com.ulfric.core.modules.ModuleNameplates;
import com.ulfric.core.modules.ModuleTrash;
import com.ulfric.core.modules.ModuleVanishInterface;
import com.ulfric.core.modules.ModuleWelcome;
import com.ulfric.core.scoreboard.ModuleScoreboardImplementation;
import com.ulfric.lib.bukkit.module.Plugin;

public class Core extends Plugin {

	@Override
	public void onFirstEnable()
	{
		this.addModule(new ModuleEconomyInterface());
		this.addModule(new ModuleWelcome());
		this.addModule(new ModuleScoreboardImplementation());
		this.addModule(new ModuleNameplates());
		this.addModule(new ModuleTrash());
		this.addModule(new ModuleVanishInterface());
		//this.addModule(new ModulePlayerSigns());
		//this.addModule(new ModuleBackpack()); // TODO: Finish this module
	}

}
