package com.ulfric.core.enchant;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

public final class EnchantmentAutoSmelt extends Enchantment {

	public static final EnchantmentAutoSmelt INSTANCE = new EnchantmentAutoSmelt();

	private EnchantmentAutoSmelt()
	{
		super("Autosmelt", 240, 1, ImmutableList.of(33));
	}

}