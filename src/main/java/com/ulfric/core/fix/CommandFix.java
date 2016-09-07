package com.ulfric.core.fix;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.economy.ModuleEconomy;
import com.ulfric.lib.coffee.economy.OfflineBankAccount;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;

public class CommandFix extends Command {

	private final ModuleFix base;

	public CommandFix(ModuleFix base)
	{
		super("fix", base, "repair");
		this.base = base;

		super.addEnforcer(Enforcers.IS_PLAYER, "fix-must-be-player");
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

		CurrencyAmount cost = this.base.getAmount();

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
