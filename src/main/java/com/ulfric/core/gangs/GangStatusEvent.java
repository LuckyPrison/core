package com.ulfric.core.gangs;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerEvent;

class GangStatusEvent extends PlayerEvent {

	GangStatusEvent(Player player, Gang gang)
	{
		super(player);
		this.gang = gang;
	}

	private final Gang gang;

	public Gang getGang()
	{
		return this.gang;
	}

}