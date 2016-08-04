package com.ulfric.core.scoreboard;

import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementVanish extends ScoreboardElement {

	public ElementVanish(Scoreboard board)
	{
		super(board, "vanish");
	}

	@Override
	public String getText()
	{
		if (this.getPlayer().isNotVanished()) return null;

		return "core.scoreboard_vanish";
	}

}