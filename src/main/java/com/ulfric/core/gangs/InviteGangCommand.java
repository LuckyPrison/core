package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;

abstract class InviteGangCommand extends GangCommand {

	public InviteGangCommand(String name, ModuleBase owner, String... aliases)
	{
		super(name, GangRank.OFFICER, owner, aliases);

		this.addArgument(OfflinePlayer.ARGUMENT);
	}

}