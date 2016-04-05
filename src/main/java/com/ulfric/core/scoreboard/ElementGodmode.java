package com.ulfric.core.scoreboard;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.scoreboard.UpdatableScoreboardElement;

class ElementGodmode extends UpdatableScoreboardElement {

	public ElementGodmode()
	{
		super(null, "godmode");
	}

	@Override
	public String get(Player player)
	{
		if (!player.isInvulnerable()) return null;

		return "core.scoreboard_godmode";
	}

}