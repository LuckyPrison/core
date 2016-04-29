package com.ulfric.core.scoreboard;

import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementPlayercount extends ScoreboardElement {

	public ElementPlayercount(Scoreboard board)
	{
		super(board, "playercount");
	}

	@Override
	public String getText()
	{
		//if (!player.hasPermission("core.scoreboard.playercount")) return null;

		return String.valueOf(PlayerUtils.countOnlinePlayers());
	}

}