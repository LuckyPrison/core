package com.ulfric.core.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public class EnchantmentNeverbreaking extends PrisonEnchantment {

	private static final EnchantmentNeverbreaking INSTANCE = new EnchantmentNeverbreaking();
	public static EnchantmentNeverbreaking get() { return EnchantmentNeverbreaking.INSTANCE; }

	private EnchantmentNeverbreaking()
	{
		super(202, "NEVERBREAKING");
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