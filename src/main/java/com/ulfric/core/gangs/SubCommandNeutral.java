package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.module.ModuleBase;

class SubCommandNeutral extends RelationGangCommand {

	public SubCommandNeutral(ModuleBase owner)
	{
		super("neutral", GangRank.OFFICER, Relation.NEUTRAL, false, owner);
	}

}