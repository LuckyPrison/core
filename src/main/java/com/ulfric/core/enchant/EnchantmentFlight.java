package com.ulfric.core.enchant;

import com.google.common.collect.ImmutableList;

public final class EnchantmentFlight extends ToolEnchantment {

	public static final EnchantmentFlight INSTANCE = new EnchantmentFlight();

	private EnchantmentFlight()
	{
		super("Flight", 241, 1, ImmutableList.of());
	}

}