package com.ulfric.core.scoreboard;

import javax.annotation.Nonnull;

import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementPlayercount extends ScoreboardElement {

	public ElementPlayercount(@Nonnull Scoreboard board)
	{
		super(board, "playercount");
	}

	@Override
	public String getText()
	{
		if (!this.getPlayer().hasPermission("core.scoreboard.playercount")) return null;

		return String.valueOf(PlayerUtils.countOnlinePlayers());
	}

}