package com.ulfric.core.economy;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.coffee.economy.OfflineBankAccount;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

class CommandPay extends Command {

	public CommandPay(ModuleBase module)
	{
		super("pay", module);
		this.addArgument(CurrencyAmount.ARGUMENT);
		this.addArgument(OfflinePlayer.ARGUMENT_ASYNC);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		OfflinePlayer player = (OfflinePlayer) this.getObject("offline-player");

		if (player.getUniqueId().equals(sender.getUniqueId()))
		{
			sender.sendLocalizedMessage("economy.pay_specify_player_self");

			return;
		}

		CurrencyAmount amount = (CurrencyAmount) this.getObject("price");
		Currency currency = amount.getCurrency();
		long amt = amount.getAmount();

		String payeeName = player.getName();
		UUID uuid = sender.getUniqueId();

		if (uuid == null)
		{
			if (!sender.hasPermission("pay.console"))
			{
				sender.sendLocalizedMessage("economy.pay_cannot_charge");

				return;
			}
		}

		else
		{
			if (!currency.isPayable())
			{
				sender.sendLocalizedMessage("economy.currency_unpayable", currency.getName());

				return;
			}

			BankAccount senderAccount = Bank.getOnlineAccount(uuid);

			long senderBalance = senderAccount.getBalance(currency);

			long difference = amt - senderBalance;
			if (difference > 0)
			{
				sender.sendLocalizedMessage("economy.pay_cannot_afford", new MoneyFormatter(difference).dualFormatWord());

				return;
			}

			try
			{
				senderAccount.take(amount, "Payment to " + payeeName).get();
			}
			catch (InterruptedException|ExecutionException e1)
			{
				e1.printStackTrace();

				return;
			}
		}

		String senderName = sender.getName();

		OfflineBankAccount recipientAccount = Bank.getAccount(player.getUniqueId());

		// No get here on purpose
		recipientAccount.give(amount, "Payment from " + senderName);

		String amtFormat = new MoneyFormatter(amt).dualFormatWord().toString();

		sender.sendLocalizedMessage("economy.payment_sent", payeeName, amtFormat);

		Player recipient = player.toPlayer();

		if (recipient == null) return;

		recipient.sendLocalizedMessage("economy.payment_received", senderName, amtFormat);
	}

}