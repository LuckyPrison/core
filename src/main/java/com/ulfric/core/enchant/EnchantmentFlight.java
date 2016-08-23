package com.ulfric.core.enchant;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

public final class EnchantmentFlight extends Enchantment {

	public static final EnchantmentFlight INSTANCE = new EnchantmentFlight();

	private EnchantmentFlight()
	{
		super("Flight", 241, 1, ImmutableList.of());
	}

}