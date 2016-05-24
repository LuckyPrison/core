package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.MoneyFormatter;
import com.ulfric.lib.coffee.economy.Price;
import com.ulfric.lib.coffee.function.Result;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.Unique;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class ModulePay extends Module {

	public ModulePay()
	{
		super("pay", "Pay command module", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new PaymentCommand().addArgument(Price.ARGUMENT).addArgument(OfflinePlayer.ARGUMENT));
	}

	private class PaymentCommand extends Command
	{
		protected PaymentCommand()
		{
			super("payment", ModulePay.this, "pay");
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();
			// TODO currently this only returns a valid offline player if the player has played on the same server instance, which will rarely be true.
			// Possibly add a BankAccount argument rather than an OfflinePlayer one?
			OfflinePlayer target = (OfflinePlayer) this.getObject("offline-player");

			if (sender.equals(target))
			{
				sender.sendLocalizedMessage("system.specify_nonself_offline_player");

				return;
			}

			BankAccount account = Bank.getAccount(target.getUniqueId());

			if (account == null)
			{
				sender.sendLocalizedMessage("economy.specify_valid_account_holder");

				return;
			}

			Price price = (Price) this.getObject("price");

			String moneyString = null;

			if (sender instanceof Unique)
			{
				BankAccount senderAccount = Bank.getAccount(((Unique) sender).getUniqueId());

				String targetName = target.getName();

				Result result = senderAccount.take(price, "Payment to " + targetName);

				if (result.isFailure())
				{
					sender.sendLocalizedMessage("economy.payment_missing_amount", result.getMessage(), targetName);

					return;
				}

				moneyString = new MoneyFormatter(price.getAmount()).duelFormatWord().toString();

				sender.sendLocalizedMessage("economy.payment_sent", moneyString);
			}

			String senderName = sender.getName();

			account.give(price, "Payment from " + senderName);

			if (!(sender instanceof Player)) return;

			sender.sendLocalizedMessage("economy.payment_received", moneyString, senderName);
		}
	}

}