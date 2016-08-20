package com.ulfric.core.gangs;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerEvent;

class GangStatusEvent extends PlayerEvent {

	GangStatusEvent(Player player, Gang oldGang, Gang newGang)
	{
		super(player);
		this.oldGang = oldGang;
		this.newGang = newGang;
	}

	private final Gang oldGang;
	private final Gang newGang;

	public Gang getOldGang()
	{
		return this.oldGang;
	}

	public Gang getNewGang()
	{
		return this.newGang;
	}

}