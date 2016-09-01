package com.ulfric.core.rankup;

import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;
import com.ulfric.lib.craft.string.ChatUtils;

final class ElementMine extends ScoreboardElement {

	ElementMine(Scoreboard board)
	{
		super(board, "mine");
	}

	@Override
	public String getText(Player updater)
	{
		Rankup rankup = Rankups.INSTANCE.getActive(updater);

		if (rankup == null) return null;

		Group group = rankup.getOld();

		if (group == null) return null;

		return Strings.format(ChatUtils.color("&a{0} (&e/mine&a)"), group.getName());
	}

}