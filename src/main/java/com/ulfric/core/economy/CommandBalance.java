package com.ulfric.core.economy;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandBalance extends Command {

	public CommandBalance(ModuleBase module)
	{
		super("bal", module, "money", "cash", "moneymoneymoneybitches");
		this.addArgument(Currency.ARGUMENT);
		this.addOptionalArgument(OfflinePlayer.ARGUMENT);
		this.addEnforcer(Player.class::isInstance, "economy.player_only");
	}

	@Override
	public void run()
	{
		Currency currency = (Currency) getObject(Currency.ARGUMENT);
		OfflinePlayer player = (Player) getObject(OfflinePlayer.ARGUMENT);
		if (player == null)
		{
			player = (OfflinePlayer) getSender();
		}
		if (player.isOnline())
		{
			long balance = Bank.getOnlineAccount(player.getUniqueId()).getBalance(currency);
			getSender().sendLocalizedMessage("economy.bal", player.getName(), new MoneyFormatter(balance).dualFormatWord());
		}
		else
		{
			final String playerName = player.getName();
			final Player sender = (Player) getSender();
			Bank.getAccount(player.getUniqueId()).retrieveBalance(currency).whenComplete(ThreadUtils::runOnMain, (bal, error) -> {
				Long balance = bal == null ? 0L : bal;
				if (error != null)
				{
					error.printStackTrace();
				}
				if (sender.isValid())
				{
					sender.sendLocalizedMessage("economy.bal", playerName, new MoneyFormatter(balance).dualFormatWord());
				}
			});
		}

	}

}