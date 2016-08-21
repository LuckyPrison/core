package com.ulfric.core.combattag;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerEvent;

public final class PlayerKilledByCombatTagEvent extends PlayerEvent {

	PlayerKilledByCombatTagEvent(Player player, CombatTag tag)
	{
		super(player);

		this.tag = tag;
	}

	private final CombatTag tag;

	public CombatTag getTag()
	{
		return this.tag;
	}

}