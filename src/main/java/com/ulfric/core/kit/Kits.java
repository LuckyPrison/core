package com.ulfric.core.kit;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

public enum Kits {

	INSTANCE;

	private final Map<String, Kit> kits = Maps.newHashMap();

	public void registerKit(Kit kit)
	{
		this.kits.put(kit.getName().toLowerCase(), kit);
	}

	public Kit getByName(String name)
	{
		String lower = name.toLowerCase();

		Kit kit = this.kits.get(lower);

		if (kit != null) return kit;

		int lowest = 2;

		for (Entry<String, Kit> entry : this.kits.entrySet())
		{
			int distance = StringUtils.getLevenshteinDistance(lower, entry.getKey(), lowest);

			if (distance == -1) continue;

			lowest = distance;
			kit = entry.getValue();

			if (distance != 0) continue;

			break;
		}

		return kit;
	}

	void clear()
	{
		this.kits.clear();
	}

}