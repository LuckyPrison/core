package com.ulfric.core.rankup;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerEvent;

public final class PlayerRankupEvent extends PlayerEvent {

	PlayerRankupEvent(Player player)
	{
		super(player);
	}

}