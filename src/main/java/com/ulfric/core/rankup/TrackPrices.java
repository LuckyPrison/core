package com.ulfric.core.rankup;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.npermission.Group;

final class TrackPrices {

	private final Map<Group, CurrencyAmount> trackPrices = Maps.newLinkedHashMap();
	private Group first;
	private CurrencyAmount lowest;

	public void putPrice(Group group, CurrencyAmount amount)
	{
		this.trackPrices.put(group, amount);

		if (this.lowest == null || this.lowest.getAmount() < amount.getAmount())
		{
			this.first = group;
			this.lowest = amount;
		}
	}

	public int size()
	{
		return this.trackPrices.size();
	}

	public CurrencyAmount getPrice(Group group)
	{
		return this.trackPrices.get(group);
	}

	public CurrencyAmount getFirstPrice()
	{
		return this.lowest;
	}

	public Group getFirstGroup()
	{
		return this.first;
	}

	public List<Group> getGroups()
	{
		return Lists.newArrayList(this.trackPrices.keySet());
	}

}