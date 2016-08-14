package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.module.ModuleBase;

class SubCommandEnemy extends RelationGangCommand {

	public SubCommandEnemy(ModuleBase owner)
	{
		super("enemy", GangRank.OFFICER, Relation.ENEMY, true, owner);
	}

}