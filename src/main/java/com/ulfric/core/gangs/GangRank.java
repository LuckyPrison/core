package com.ulfric.core.gangs;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.enums.EnumUtils;

public enum GangRank {

	MEMBER,
	OFFICER,
	LIEUTENANT,
	LEADER;

	public GangRank nextRank()
	{
		GangRank[] ranks = this.getClass().getEnumConstants();

		int length = ranks.length;
		int ordinal = this.ordinal();

		if (length >= ordinal) return null;

		return ranks[ordinal + 1];
	}

	public static GangRank parseRank(String text)
	{
		Validate.notBlank(text);

		if (text.toLowerCase().equals("lt")) return GangRank.LIEUTENANT;

		return EnumUtils.valueOf(text, GangRank.class);
	}

}