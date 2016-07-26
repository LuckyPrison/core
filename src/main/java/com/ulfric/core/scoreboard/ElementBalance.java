package com.ulfric.core.scoreboard;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementBalance extends ScoreboardElement {

	public ElementBalance(Scoreboard board)
	{
		super(board, "balance");
	}

	@Override
	public String getText()
	{
		return new MoneyFormatter(Bank.getOnlineAccount(this.getPlayer().getUniqueId()).getBalance(Currency.getDefaultCurrency())).letterFormat().toString();
	}

}