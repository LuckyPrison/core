package com.ulfric.core.mines;

import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.region.Region;

public enum Mines {

	INSTANCE;

	private final Map<String, Mine> mines = new CaseInsensitiveMap<>();
	private final Map<Region, Mine> minesByRegion = Maps.newHashMap();

	public Mine getByName(String name)
	{
		return this.mines.get(name);
	}

	public Mine getByRegion(Region region)
	{
		if (this.minesByRegion.containsKey(region))
		{
			return this.minesByRegion.get(region);
		}

		for (Mine mine : this.mines.values())
		{
			if (!mine.containsRegion(region)) continue;

			this.minesByRegion.put(region, mine);

			return mine;
		}

		this.minesByRegion.put(region, null);

		return null;
	}

	public void registerMine(Mine mine)
	{
		this.mines.put(mine.getName(), mine);
	}

	void clear()
	{
		this.mines.clear();
		this.minesByRegion.clear();
	}

}