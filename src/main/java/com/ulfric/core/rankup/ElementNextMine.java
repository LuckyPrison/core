package com.ulfric.core.rankup;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;
import com.ulfric.lib.craft.string.ChatUtils;

final class ElementNextMine extends ScoreboardElement {

	public ElementNextMine(Scoreboard board)
	{
		super(board, "next-mine-element");
	}

	@Override
	public String getText(Player updater)
	{
		Rankup rankup = Rankups.INSTANCE.getActive(updater);

		if (rankup == null) return null;

		CurrencyAmount cost = rankup.getCost();

		if (cost == null) return null;

		long balance = Bank.getOnlineAccount(updater.getUniqueId()).getBalance(cost.getCurrency());

		long amount = cost.getAmount();

		if (balance >= amount)
		{
			return ChatUtils.color("&d|||||||||||||||||||| 100%");
		}

		long percent = NumberUtils.percentage(amount, balance);

		if (percent == 0)
		{
			return ChatUtils.color("&7|||||||||||||||||||| 0%");
		}

		StringBuilder builder = new StringBuilder();

		int count = (int) Math.max(Math.min(percent, 19), 1);

		int remainder = 20 - count;

		builder.append(ChatUtils.color("&a"));

		for (int x = 0; x < count; x++)
		{
			builder.append('|');
		}

		builder.append(ChatUtils.color("&7"));

		for (int x = 0; x < remainder; x++)
		{
			builder.append('|');
		}

		builder.append(' ');
		builder.append(percent);
		builder.append('%');

		return builder.toString();
	}

}