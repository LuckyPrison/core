package com.ulfric.core;

import com.ulfric.core.achievement.ModuleAchievements;
import com.ulfric.core.backpack.ModuleBackpack;
import com.ulfric.core.chat.ModuleChat;
import com.ulfric.core.combattag.ModuleCombatTag;
import com.ulfric.core.control.ModuleClearChat;
import com.ulfric.core.control.ModuleCloseInventory;
import com.ulfric.core.control.ModuleControl;
import com.ulfric.core.economy.ModuleEconomyInterface;
import com.ulfric.core.enchant.ModuleEnchants;
import com.ulfric.core.gangs.ModuleGangs;
import com.ulfric.core.kit.ModuleKits;
import com.ulfric.core.luckyblocks.ModuleLuckyBlocks;
import com.ulfric.core.lwe.ModuleLWE;
import com.ulfric.core.mines.ModuleMines;
import com.ulfric.core.modules.ModuleBeheading;
import com.ulfric.core.modules.ModuleCandy;
import com.ulfric.core.modules.ModuleColoredSigns;
import com.ulfric.core.modules.ModuleEmailInterface;
import com.ulfric.core.modules.ModuleEntityDisabler;
import com.ulfric.core.modules.ModuleGameModeInterface;
import com.ulfric.core.modules.ModuleGodmodeInterface;
import com.ulfric.core.modules.ModuleNameplates;
import com.ulfric.core.modules.ModulePrivateMessaging;
import com.ulfric.core.modules.ModuleSpeed;
import com.ulfric.core.modules.ModuleSpeedyGonzales;
import com.ulfric.core.modules.ModuleStackSize;
import com.ulfric.core.modules.ModuleTrample;
import com.ulfric.core.modules.ModuleTrash;
import com.ulfric.core.modules.ModuleVanishInterface;
import com.ulfric.core.modules.ModuleWelcome;
import com.ulfric.core.playerlist.ModulePlayerList;
import com.ulfric.core.rankup.ModuleRankup;
import com.ulfric.core.regions.ModuleRegionInterface;
import com.ulfric.core.settings.ModuleSettings;
import com.ulfric.core.teleport.ModuleTeleport;
import com.ulfric.lib.bukkit.module.Plugin;

public class Core extends Plugin {

	@Override
	public void onFirstEnable()
	{
		this.addModule(new ModuleSettings());
		this.addModule(new ModuleAchievements());
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
		this.addModule(new ModuleControl());
		this.addModule(new ModuleGangs());
		this.addModule(new ModuleCombatTag());
		this.addModule(new ModuleGameModeInterface());
		this.addModule(new ModuleEntityDisabler());
		this.addModule(new ModuleChat());
		this.addModule(new ModuleTeleport());
		this.addModule(new ModuleEnchants());
		this.addModule(new ModuleRegionInterface());
		this.addModule(new ModuleMines());
		this.addModule(new ModuleKits());
		this.addModule(new ModuleRankup());
		this.addModule(new ModuleLuckyBlocks());
		this.addModule(new ModuleSpeedyGonzales());
		this.addModule(new ModuleLWE());
		this.addModule(new ModuleColoredSigns());
		this.addModule(new ModuleSpeed());
		this.addModule(new ModuleCandy());
		this.addModule(new ModuleTrample());
		this.addModule(ModuleBackpack.getInstance());
		this.addModule(new ModuleStackSize());
	}

}
