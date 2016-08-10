package com.ulfric.core.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public class EnchantmentSpeedygonzales extends PrisonEnchantment {

	private static final EnchantmentSpeedygonzales INSTANCE = new EnchantmentSpeedygonzales();
	public static EnchantmentSpeedygonzales get() { return EnchantmentSpeedygonzales.INSTANCE; }

	private EnchantmentSpeedygonzales()
	{
		super(203, "SPEEDY_GONZALES");
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel()
	{
		return 1;
	}

}