package com.ulfric.core.scoreboard;

import javax.annotation.Nonnull;

import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementGodmode extends ScoreboardElement {

	public ElementGodmode(@Nonnull Scoreboard board)
	{
		super(board, "godmode");
	}

	@Override
	public String getText()
	{
		if (!this.getScoreboard().getPlayer().health().isInvulnerable()) return null;

		return "core.scoreboard_godmode";
	}

}