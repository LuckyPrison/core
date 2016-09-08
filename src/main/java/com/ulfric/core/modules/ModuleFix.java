package com.ulfric.core.modules;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.economy.ModuleEconomy;
import com.ulfric.lib.coffee.economy.OfflineBankAccount;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;

public class ModuleFix extends Module {

	private CurrencyAmount amount;

	public ModuleFix()
	{
		super("fix", "/fix command", "1.0.0", "insou");
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = super.getModuleConfig();
		MutableDocument document = config.getRoot();
		if (!document.contains("cost"))
		{
			document.set("cost", CurrencyAmount.of(Currency.getDefaultCurrency(), 100L).toString());
			config.save();
		}
		this.amount = CurrencyAmount.valueOf(document.getString("cost"));

		this.addCommand(new CommandFix());
	}

	public CurrencyAmount getAmount()
	{
		return amount;
	}

	private final class CommandFix extends Command {

		CommandFix()
		{
			super("fix", ModuleFix.this, "repair");

			this.addEnforcer(Enforcers.IS_PLAYER, "fix-must-be-player");
		}

		@Override
		public void run()
		{
			Player player = (Player) super.getSender();

			ItemStack item = player.getMainHand();

			if (!player.hasPermission("fix.fix"))
			{
				player.sendLocalizedMessage("fix-no-permission");

				return;
			}

			if (item.getType().isBlock() || item.getType().getMaxDurability() < 1)
			{
				player.sendLocalizedMessage("fix-invalid-item");

				return;
			}

			if (item.getDurability() == 0)
			{
				player.sendLocalizedMessage("fix-already-fixed");

				return;
			}

			OfflineBankAccount account = ModuleEconomy.get().getAccount(player.getUniqueId());

			CurrencyAmount cost = ModuleFix.this.getAmount();

			account.retrieveBalance(cost.getCurrency()).whenComplete((balance, throwable) ->
			{
				if (balance < cost.getAmount())
				{
					player.sendLocalizedMessage("fix.not-enough-money", balance, cost.getAmount());

					return;
				}
				account.take(cost, "fix").whenComplete((balance2, throwable2) ->
						player.getMainHand().setDurability(0));

				player.sendLocalizedMessage("fix-fixed", cost.getAmount(), (balance - cost.getAmount()));
			});
		}

	}


}
