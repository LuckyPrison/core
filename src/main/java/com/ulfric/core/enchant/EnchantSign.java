package com.ulfric.core.enchant;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.SignListener;
import com.ulfric.lib.craft.event.player.PlayerUseSignEvent.Action;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemStack.EnchantList;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.inventory.item.enchant.Enchant;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

final class EnchantSign extends SignListener {

	public EnchantSign(ModuleBase owner)
	{
		super(owner, "enchant", Action.RIGHT_CLICK);
	}

	@Override
	public void handle(Player player, Sign sign)
	{
		ItemStack item = player.getMainHand();

		if (item == null)
		{
			player.sendLocalizedMessage("enchant-air");

			return;
		}

		Material material = item.getType();

		if (material == null || material.ordinal() == 0)
		{
			player.sendLocalizedMessage("enchant-air");

			return;
		}

		Enchantment enchant = Enchantment.parse(sign.getLine(2));

		if (!enchant.canEnchant(item))
		{
			player.sendLocalizedMessage("enchant-not-compatible");

			return;
		}

		CurrencyAmount price = CurrencyAmount.valueOf(sign.getLine(3));
		BankAccount account = null;

		if (price != null)
		{
			account = Bank.getOnlineAccount(player.getUniqueId());

			if (account.getBalance(price.getCurrency()) < price.getAmount())
			{
				// TODO we could play a red particle effect over the sign for the player

				return;
			}
		}

		int amount = this.getAmount(sign.getLine(1));
		int max = enchant.getMax();

		EnchantList enchants = item.enchants();

		int currentLevel = enchants.getLevel(enchant);

		if (currentLevel >= max)
		{
			// TODO maxed msg

			return;
		}

		int total = currentLevel + amount;

		if (total > max)
		{
			/*final int originalAmount = amount;

			if (price != null)
			{
				
			}*/

			return; // TODO be fancy instead
		}

		if (account != null)
		{
			account.take(price, "Purchase of Enchantment");
		}

		enchants.add(Enchant.of(enchant, total));

		// TODO sound, particle, message
	}

	private int getAmount(String string)
	{
		if (StringUtils.isBlank(string)) return 1;

		String str = string.trim().replace("+", "").replace(",", "");

		return Math.max(NumberUtils.getInt(NumberUtils.parseInteger(str)), 1);
	}

}