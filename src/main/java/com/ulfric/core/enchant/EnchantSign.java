package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.SignListener;
import com.ulfric.lib.craft.event.player.PlayerUseSignEvent.Action;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;

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
			player.sendLocalizedMessage("enchant.air");

			return;
		}

		Material material = item.getType();

		if (material == null || material.ordinal() == 0)
		{
			player.sendLocalizedMessage("enchant.air");

			return;
		}

		
	}

}