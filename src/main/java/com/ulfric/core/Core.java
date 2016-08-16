package com.ulfric.core;

import com.ulfric.core.combattag.ModuleCombatTag;
import com.ulfric.core.control.ModuleClearChat;
import com.ulfric.core.control.ModuleCloseInventory;
import com.ulfric.core.control.ModulePunishments;
import com.ulfric.core.economy.ModuleEconomyInterface;
import com.ulfric.core.gangs.ModuleGangs;
import com.ulfric.core.modules.ModuleBeheading;
import com.ulfric.core.modules.ModuleEmailInterface;
import com.ulfric.core.modules.ModuleEntityDisabler;
import com.ulfric.core.modules.ModuleGameModeInterface;
import com.ulfric.core.modules.ModuleGodmodeInterface;
import com.ulfric.core.modules.ModuleNameplates;
import com.ulfric.core.modules.ModulePrivateMessaging;
import com.ulfric.core.modules.ModuleTrash;
import com.ulfric.core.modules.ModuleVanishInterface;
import com.ulfric.core.modules.ModuleWelcome;
import com.ulfric.core.playerlist.ModulePlayerList;
import com.ulfric.lib.bukkit.module.Plugin;

public class Core extends Plugin {

	@Override
	public void onFirstEnable()
	{
		this.addModule(new ModuleEconomyInterface());
		this.addModule(new ModuleWelcome());
		this.addModule(new ModulePlayerList());
		this.addModule(new ModuleNameplates());
		this.addModule(new ModuleTrash());
		this.addModule(new ModuleVanishInterface());
		this.addModule(new ModuleGodmodeInterface());
		this.addModule(new ModuleClearChat());
		this.addModule(new ModuleCloseInventory());
		this.addModule(new ModuleEmailInterface());
		this.addModule(new ModuleBeheading());
		this.addModule(new ModulePrivateMessaging());
		this.addModule(new ModulePunishments());
		this.addModule(new ModuleGangs());
		this.addModule(new ModuleCombatTag());
		this.addModule(new ModuleGameModeInterface());
		this.addModule(new ModuleEntityDisabler());
		//this.addModule(new ModuleBackpack()); // TODO: Finish this module
	}

}
