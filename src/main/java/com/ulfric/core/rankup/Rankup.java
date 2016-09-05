package com.ulfric.core.rankup;

import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.npermission.Group;

public final class Rankup {

	Rankup(Group oldGroup, Group newGroup, CurrencyAmount price)
	{
		this.oldGroup = oldGroup;
		this.newGroup = newGroup;
		this.price = price;
	}

	private final Group oldGroup;
	private final Group newGroup;
	private final CurrencyAmount price;

	public Group getOldGroup()
	{
		return this.oldGroup;
	}

	public Group getNewGroup()
	{
		return this.newGroup;
	}

	public CurrencyAmount getPrice()
	{
		return this.price;
	}

}