package com.ulfric.core.reward;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.craft.entity.player.Player;

final class MoneyReward implements Reward {

	static MoneyReward valueOf(CurrencyAmount amount)
	{
		Validate.notNull(amount);

		return new MoneyReward(amount);
	}

	private MoneyReward(CurrencyAmount amount)
	{
		this.amount = amount;
		this.format = amount.toFormatter().dualFormatWord().toString();
	}

	private final CurrencyAmount amount;
	private final String format;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		Bank.getOnlineAccount(player.getUniqueId()).give(this.amount, reason);
		player.sendLocalizedMessage("luckyblock-money", this.format);
	}

}