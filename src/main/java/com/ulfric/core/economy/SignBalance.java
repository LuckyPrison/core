package com.ulfric.core.economy;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.SignListener;

class SignBalance extends SignListener {

	public SignBalance(ModuleBase owner)
	{
		super(owner, "balance");
	}

	@Override
	public void handle(Player player, Sign sign)
	{
		Currency currency = Currency.getCurrency(sign.getLine(1));

		if (currency == null)
		{
			currency = Currency.getDefaultCurrency();
		}

		player.sendLocalizedMessage("economy.balance_self", player.getName(), new MoneyFormatter(Bank.getOnlineAccount(player.getUniqueId()).getBalance(currency)).dualFormatWord());
	}

}