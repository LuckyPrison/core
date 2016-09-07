package com.ulfric.core.enchant;

import com.google.common.collect.ImmutableList;

public final class EnchantmentAutoSell extends ToolEnchantment {

	public static final EnchantmentAutoSell INSTANCE = new EnchantmentAutoSell();

	private EnchantmentAutoSell()
	{
		super("Autosell", 244, 3, ImmutableList.of());
	}

}