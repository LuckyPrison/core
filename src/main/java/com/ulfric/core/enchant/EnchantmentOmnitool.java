package com.ulfric.core.enchant;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

public final class EnchantmentOmnitool extends Enchantment {

	public static final EnchantmentOmnitool INSTANCE = new EnchantmentOmnitool();

	private EnchantmentOmnitool()
	{
		super("Omnitool", 242, 1, ImmutableList.of());
	}

	@Override
	public boolean canEnchant(ItemStack item)
	{
		if (!super.canEnchant(item)) return false;

		return item.getType().getName().startsWith("DIAMOND_");
	}

}