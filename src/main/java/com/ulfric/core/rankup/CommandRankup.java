package com.ulfric.core.rankup;

import java.util.Arrays;
import java.util.List;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandRankup extends Command {

	CommandRankup(ModuleBase owner)
	{
		super("rankup", owner, "nextrank");

		this.addEnforcer(Enforcers.IS_PLAYER, "rankup-must-be-player");
	}

	private final List<String> randomEncouragement = Arrays.asList("Nice work!", "Good job!", "Great work!", "Nice job!", "Good work!", "Good job!", "Amazing work!", "Stellar job!", "Excellent work!", "Excellent job!", "Very nice!", "You'll be at the top in no time!", "Congrats!");

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		Rankup rankup = ModuleRankup.INSTANCE.getNextRank(player);

		if (rankup == null)
		{
			player.sendLocalizedMessage("rankup-no-rank-found");

			return;
		}

		Group next = rankup.getNewGroup();
		String name = next.getName();
		CurrencyAmount price = rankup.getPrice();
		BankAccount account = null;

		if (price != null)
		{
			Currency currency = price.getCurrency();

			account = Bank.getOnlineAccount(player.getUniqueId());

			long balance = account.getBalance(currency);

			long diff = price.getAmount() - balance;

			if (diff > 0)
			{
				player.sendLocalizedMessage("rankup-cannot-afford", new MoneyFormatter(currency.getFormat(), diff).dualFormatWord(), name);

				return;
			}
		}

		if (account != null)
		{
			account.take(price, "Rankup to " + name);
		}

		player.swapGroups(rankup.getOldGroup(), rankup.getNewGroup());

		new PlayerRankupEvent(player).fire();

		player.sendLocalizedMessage("rankup-success", name, RandomUtils.randomValue(this.randomEncouragement));
	}

}