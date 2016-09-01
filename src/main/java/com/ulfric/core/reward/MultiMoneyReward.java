package com.ulfric.core.reward;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.craft.entity.player.Player;

final class MultiMoneyReward implements Reward {

	static MultiMoneyReward valueOf(List<CurrencyAmount> amounts)
	{
		Validate.notEmpty(amounts);
		Validate.noNullElements(amounts);

		return new MultiMoneyReward(ImmutableList.copyOf(amounts));
	}

	private MultiMoneyReward(List<CurrencyAmount> amounts)
	{
		this.amounts = amounts;
	}

	private final List<CurrencyAmount> amounts;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		BankAccount account = Bank.getOnlineAccount(player.getUniqueId());

		for (CurrencyAmount amount : this.amounts)
		{
			account.give(amount, reason);
		}
	}

}