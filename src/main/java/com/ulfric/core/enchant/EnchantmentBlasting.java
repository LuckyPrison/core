package com.ulfric.core.enchant;

import com.google.common.collect.ImmutableList;

public final class EnchantmentBlasting extends ToolEnchantment {

	public static final EnchantmentBlasting INSTANCE = new EnchantmentBlasting();

	private EnchantmentBlasting()
	{
		super("Blasting", 243, 5, ImmutableList.of());
	}

}