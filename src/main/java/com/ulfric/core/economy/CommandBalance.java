package com.ulfric.core.economy;

import java.util.UUID;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

class CommandBalance extends Command {

	public CommandBalance(ModuleBase module)
	{
		super("bal", module, "balance", "money", "cash");
		this.addArgument(Currency.ARGUMENT);
		this.addOptionalArgument(OfflinePlayer.ARGUMENT);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Currency currency = (Currency) getObject(Currency.ARGUMENT);
		OfflinePlayer player = (Player) getObject(OfflinePlayer.ARGUMENT);

		if (player == null)
		{
			player = (OfflinePlayer) sender;
		}

		else if (player.isOnline())
		{
			long balance = Bank.getOnlineAccount(player.getUniqueId()).getBalance(currency);

			String money = new MoneyFormatter(balance).dualFormatWord().toString();

			if (sender == player)
			{
				sender.sendLocalizedMessage("economy.balance_self", money);

				return;
			}

			sender.sendLocalizedMessage("economy.balance_other", player.getName(), money);

			return;
		}

		final String playerName = player.getName();
		final UUID uuid = player.getUniqueId();

		Bank.getAccount(uuid).retrieveBalance(currency).whenComplete(ThreadUtils::runOnMain, (bal, error) ->
		{
			long balance = bal == null ? 0L : bal;

			if (error != null)
			{
				error.printStackTrace();
			}

			sender.sendLocalizedMessage("economy.balance_other", playerName, new MoneyFormatter(balance).dualFormatWord());
		});
	}

}