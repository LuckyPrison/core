package com.ulfric.core.reward;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.craft.entity.player.Player;

public final class MoneyReward implements Reward {

	public static MoneyReward valueOf(CurrencyAmount amount)
	{
		Validate.notNull(amount);

		return new MoneyReward(amount);
	}

	private MoneyReward(CurrencyAmount amount)
	{
		this.amount = amount;
	}

	private final CurrencyAmount amount;

	@Override
	public void give(Player player, String reason)
	{
		Bank.getOnlineAccount(player.getUniqueId()).give(this.amount, reason);
	}

}