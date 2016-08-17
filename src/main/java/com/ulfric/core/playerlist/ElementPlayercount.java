package com.ulfric.core.playerlist;

import java.text.NumberFormat;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementPlayercount extends ScoreboardElement {

	public ElementPlayercount(Scoreboard board, float multiplier)
	{
		super(board, "playercount");

		this.multiplier = multiplier;
	}

	private final float multiplier;

	@Override
	public String getText(Player updater)
	{
		if (!updater.hasPermission("core.scoreboard.playercount")) return null;

		return String.valueOf(NumberFormat.getIntegerInstance().format((int) (PlayerUtils.countOnlinePlayers() * this.multiplier)));
	}

}