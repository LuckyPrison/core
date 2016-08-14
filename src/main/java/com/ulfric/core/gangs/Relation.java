package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.enums.EnumUtils;

public enum Relation {

	NEUTRAL,
	ALLY,
	ENEMY;

	public static Relation parseRelation(String context)
	{
		if (context.toLowerCase().equals("none")) return Relation.NEUTRAL;

		return EnumUtils.valueOf(context, Relation.class, 3);
	}

}