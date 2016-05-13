package com.ulfric.core.scoreboard;

import javax.annotation.Nonnull;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

class ElementBalance extends ScoreboardElement {

	public ElementBalance(@Nonnull Scoreboard board)
	{
		super(board, "balance");
	}

	@Override
	public String getText()
	{
		return new MoneyFormatter(Bank.getAccount(this.getPlayer().getUniqueId()).getBalance(Currency.valueOf('$'))).letterFormat().toString();
	}

}