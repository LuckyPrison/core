package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.module.ModuleBase;

class SubCommandAlly extends RelationGangCommand {

	public SubCommandAlly(ModuleBase owner)
	{
		super("ally", GangRank.OFFICER, Relation.ALLY, owner);
	}

}