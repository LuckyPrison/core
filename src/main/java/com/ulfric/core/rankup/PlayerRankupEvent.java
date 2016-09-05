package com.ulfric.core.rankup;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerEvent;

public class PlayerRankupEvent extends PlayerEvent {

	protected PlayerRankupEvent(Player player)
	{
		super(player);
	}

}