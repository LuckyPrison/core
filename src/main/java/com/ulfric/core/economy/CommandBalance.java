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

final class CommandBalance extends Command {

	public CommandBalance(ModuleBase module)
	{
		super("bal", module, "balance", "money", "cash");
		this.addOptionalArgument(Currency.ARGUMENT);
		this.addOptionalArgument(OfflinePlayer.ARGUMENT);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Currency currency = (Currency) getObject(Currency.ARGUMENT.getPath());
		OfflinePlayer player = (OfflinePlayer) getObject(OfflinePlayer.ARGUMENT.getPath());

		if (player == null)
		{
			if (!(sender instanceof OfflinePlayer)) return;

			player = (OfflinePlayer) sender;
		}

		else if (player.isOnline())
		{
			long balance = Bank.getOnlineAccount(player.getUniqueId()).getBalance(currency);

			String money = new MoneyFormatter(currency.getFormat(), balance).dualFormatWord().toString();

			if (player.getUniqueId().equals(sender.getUniqueId()))
			{
				sender.sendLocalizedMessage("economy-balance", money);

				return;
			}

			sender.sendLocalizedMessage("economy-balance-other", player.getName(), money);

			return;
		}

		final String playerName = player.getName();
		final UUID uuid = player.getUniqueId();

		Bank.getAccount(uuid).retrieveBalance(currency).whenComplete(ThreadUtils::runOnMain, (bal, error) ->
		{
			if (error != null)
			{
				error.printStackTrace();
			}

			long balance = bal == null ? 0L : bal;

			sender.sendLocalizedMessage("economy-balance-other", playerName, new MoneyFormatter(currency.getFormat(), balance).dualFormatWord());
		});
	}

}