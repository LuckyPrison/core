package com.ulfric.core.reward;

import org.apache.commons.lang3.ArrayUtils;

import com.ulfric.lib.craft.entity.player.Player;

public interface Reward {

	default void give(Player player, String reason)
	{
		this.give(player, reason, ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	void give(Player player, String reason, Object... objects);

}