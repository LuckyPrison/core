package com.ulfric.core.economy;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.SignListener;

class SignBuy extends SignListener {

	public SignBuy(ModuleBase owner)
	{
		super(owner, "buy");
	}

	@Override
	public void handle(Player player, Sign sign)
	{
		CurrencyAmount price = CurrencyAmount.valueOf(sign.getLine(3));

		BankAccount acc = Bank.getOnlineAccount(player.getUniqueId());

		long amt = acc.getBalance(price.getCurrency());

		if (amt == 0) return;

		if (amt < price.getAmount()) return;

		acc.take(price, "Buysign Use");

		int amount = Integer.parseInt(sign.getLine(1));
		MaterialData data = MaterialData.of(sign.getLine(2));

		player.getInventory().addItem(data.toItem(amount));
	}

}