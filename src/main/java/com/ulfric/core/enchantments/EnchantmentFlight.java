package com.ulfric.core.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public class EnchantmentFlight extends PrisonEnchantment {

	private static final EnchantmentFlight INSTANCE = new EnchantmentFlight();
	public static EnchantmentFlight get() { return EnchantmentFlight.INSTANCE; }

	private EnchantmentFlight()
	{
		super(201, "FLIGHT");
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel()
	{
		return 2;
	}

}