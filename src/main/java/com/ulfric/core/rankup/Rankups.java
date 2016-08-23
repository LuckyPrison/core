package com.ulfric.core.rankup;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.permission.Group;
import com.ulfric.lib.coffee.permission.PermissionsManager;
import com.ulfric.lib.coffee.permission.Track;
import com.ulfric.lib.craft.entity.player.Player;

public enum Rankups {

	INSTANCE;

	private final Map<Track, Map<Group, CurrencyAmount>> rankups = Maps.newHashMap();

	public Rankup getActive(Player player)
	{
		Set<Track> tracks = player.getTracks();

		if (SetUtils.isEmpty(tracks)) return null;

		Set<Group> groups = player.getParents();

		if (SetUtils.isEmpty(groups)) return null;

		for (Track track : tracks)
		{
			for (Group group : groups)
			{
				Group next = track.getNext(group);

				if (next == null) continue;

				Map<Group, CurrencyAmount> costs = this.rankups.get(track);

				CurrencyAmount cost = costs == null ? null : costs.get(next);

				return new Rankup(track, group, next, cost);
			}
		}

		return null;
	}

	void registerRankup(Group group, CurrencyAmount amount)
	{
		for (Track track : PermissionsManager.get().getTracksForGroup(group))
		{
			Map<Group, CurrencyAmount> amounts = this.rankups.get(track);

			if (amounts == null)
			{
				amounts = Maps.newHashMap();

				this.rankups.put(track, amounts);
			}

			amounts.put(group, amount);
		}
	}

	void clear()
	{
		this.rankups.clear();
	}

}