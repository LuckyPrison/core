package com.ulfric.core.economy;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementBalance extends ScoreboardElement {

	public ElementBalance(Scoreboard board)
	{
		super(board, "balance-element");
	}

	@Override
	public String getText(Player updater)
	{
		return updater.getLocalizedMessage("balance-element-content", new MoneyFormatter(Bank.getOnlineAccount(updater.getUniqueId()).getBalance(Currency.getDefaultCurrency())).letterFormat());
	}

}