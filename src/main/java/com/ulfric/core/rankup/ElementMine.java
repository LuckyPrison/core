package com.ulfric.core.rankup;

import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

final class ElementMine extends ScoreboardElement {

	ElementMine(Scoreboard board)
	{
		super(board, "mine-element");
	}

	@Override
	public String getText(Player updater)
	{
		Group group = updater.getCurrentGroup();

		if (group == null) return null;

		return updater.getLocalizedMessage("next-mine-value", group.getName());
	}

}