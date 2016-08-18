package com.ulfric.core.economy;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.ulfric.data.DataUpdate;
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
		this.addArgument(OfflinePlayer.ARGUMENT);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		OfflinePlayer player = (OfflinePlayer) this.getObject("offline-player");

		if (sender == player)
		{
			sender.sendLocalizedMessage("economy.pay_specify_player_self");

			return;
		}

		UUID uuid = sender.getUniqueId();
		CurrencyAmount amount = (CurrencyAmount) this.getObject("price");
		Currency currency = amount.getCurrency();
		long amt = amount.getAmount();
		String payeeName = player.getName();
		MoneyFormatter amountFormat = new MoneyFormatter(amt).dualFormatLetter();

		if (uuid != null)
		{
			BankAccount account = Bank.getOnlineAccount(uuid);

			long balance = account.getBalance(currency);

			long diff = balance - amt;

			if (diff < 0)
			{
				sender.sendLocalizedMessage("economy.pay_cannot_afford", new MoneyFormatter(diff).dualFormatLetter());

				return;
			}

			DataUpdate<Long> take = account.take(amount, "Payment to " + payeeName);

			try
			{
				take.get();
			}
			catch (InterruptedException|ExecutionException exception)
			{
				exception.printStackTrace();

				return;
			}

			sender.sendLocalizedMessage("economy.pay_paid", payeeName, amountFormat, new MoneyFormatter(account.getBalance(currency)).dualFormatLetter());
		}

		Player online = player.toPlayer();
		UUID playerUuid = player.getUniqueId();
		OfflineBankAccount account = online == null ? Bank.getAccount(playerUuid) : Bank.getOnlineAccount(playerUuid);

		String senderName = sender.getName();

		account.give(amount, "Payment from " + senderName);

		if (online == null) return;

		BankAccount onlineAccount = account instanceof BankAccount ? (BankAccount) account : Bank.getOnlineAccount(playerUuid);

		if (onlineAccount == null) return;

		online.sendLocalizedMessage("economy.pay_received", senderName, amountFormat, new MoneyFormatter(onlineAccount.getBalance(amount.getCurrency())));
	}

}