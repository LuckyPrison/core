package com.ulfric.core.economy;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.economy.OfflineBankAccount;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;

final class CommandSetbalance extends Command {

	public CommandSetbalance(ModuleBase module)
	{
		super("setbalance", module, "setbal");
		this.addArgument(CurrencyAmount.ARGUMENT);
		this.addArgument(OfflinePlayer.ARGUMENT_ASYNC);

		this.addPermission("setbalance.use");
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("offline-player");
		CurrencyAmount amount = (CurrencyAmount) this.getObject("price");

		OfflineBankAccount recipientAccount = Bank.getAccount(player.getUniqueId());

		recipientAccount.set(amount, "Balance set by " + this.getSender().getName());
	}

}