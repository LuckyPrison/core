package com.ulfric.core.economy;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.ulfric.data.DataUpdate;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.coffee.economy.OfflineBankAccount;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandPay extends Command {

	public CommandPay(ModuleBase module)
	{
		super("pay", module);
		this.addArgument(CurrencyAmount.ARGUMENT);
		this.addArgument(OfflinePlayer.ARGUMENT);
	}

	@Override
	public void run()
	{
		System.out.println("1");
		CommandSender sender = this.getSender();
		UUID uuid = sender.getUniqueId();

		CurrencyAmount amount = (CurrencyAmount) this.getObject("price");
		OfflinePlayer player = (OfflinePlayer) this.getObject("offline-player");
		String payeeName = player.getName();

		MoneyFormatter amountFormat = new MoneyFormatter(amount.getAmount()).dualFormatLetter();

		if (uuid != null)
		{
			if (player.getUniqueId().equals(uuid))
			{
				sender.sendLocalizedMessage("pay.specify_player_self");

				return;
			}

			BankAccount account = Bank.getOnlineAccount(uuid);

			DataUpdate<Long> balance = account.retrieveBalance(amount.getCurrency());

			try
			{
				long bal = NumberUtils.getLong(balance.get());

				long diff = bal - amount.getAmount();

				if (diff < 0)
				{
					sender.sendLocalizedMessage("pay.missing_money", amountFormat, new MoneyFormatter(bal).dualFormatLetter(), new MoneyFormatter(diff).dualFormatLetter());

					return;
				}
			}
			catch (InterruptedException|ExecutionException exception)
			{
				exception.printStackTrace();

				return;
			}

			DataUpdate<Long> take = account.take(amount, "Payment to " + payeeName);

			take.whenComplete((l, t) ->
			{
				try
				{
					sender.sendLocalizedMessage("pay.payment_sent", payeeName, amountFormat, new MoneyFormatter(account.retrieveBalance(amount.getCurrency()).get()).dualFormatLetter());
				}
				catch (InterruptedException|ExecutionException exception)
				{
					sender.sendLocalizedMessage("pay.payment_sent_fallback", payeeName, amountFormat);

					exception.printStackTrace();
				}
			});
		}

		System.out.println("2");
		Player online = player.toPlayer();

		System.out.println("3: " + player.getName());

		OfflineBankAccount account = online == null ? Bank.getAccount(player.getUniqueId()) : Bank.getOnlineAccount(player.getUniqueId());

		try {
			System.out.println("4: " + account.retrieveBalance(amount.getCurrency()).get());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("5: " + amount.getCurrency().getName());
		System.out.println("6: " + amount.getAmount());

		String senderName = sender.getName();

		try
		{
			account.give(amount, "Payment from " + senderName).get();
		}
		catch (InterruptedException|ExecutionException exception)
		{
			exception.printStackTrace();

			return;
		}

		if (online == null) return;

		try
		{
			online.sendLocalizedMessage("pay.you_got_money", senderName, amountFormat, new MoneyFormatter(account.retrieveBalance(amount.getCurrency()).get()));
		}
		catch (InterruptedException|ExecutionException exception)
		{
			online.sendLocalizedMessage("pay.you_got_money_backup", senderName, amountFormat);

			exception.printStackTrace();
		}
	}

}