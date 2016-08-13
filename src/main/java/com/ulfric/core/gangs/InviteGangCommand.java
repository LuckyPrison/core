package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;

abstract class InviteGangCommand extends GangCommand {

	public InviteGangCommand(String name, ModuleBase owner)
	{
		super(name, GangRank.OFFICER, owner);

		this.addArgument(OfflinePlayer.ARGUMENT);
	}

}