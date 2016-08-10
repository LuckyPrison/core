package com.ulfric.core.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public class EnchantmentBlasting extends PrisonEnchantment {

	private static final EnchantmentBlasting INSTANCE = new EnchantmentBlasting();
	public static EnchantmentBlasting get() { return EnchantmentBlasting.INSTANCE; }

	private EnchantmentBlasting()
	{
		super(200, "BLASTING");
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel()
	{
		return 30;
	}

}