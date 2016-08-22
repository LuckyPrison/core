package com.ulfric.core.reward;

import com.ulfric.lib.craft.entity.player.Player;

public interface Reward {

	void give(Player player, String reason);

}