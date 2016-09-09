package com.ulfric.core.crate;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerEvent;

public final class CrateOpenEvent extends PlayerEvent {

	private final Crate crate;

	protected CrateOpenEvent(Player player, Crate crate)
	{
		super(player);

		this.crate = crate;
	}

	public Crate getCrate()
	{
		return this.crate;
	}



}
