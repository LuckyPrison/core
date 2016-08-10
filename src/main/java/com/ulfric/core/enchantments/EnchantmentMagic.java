package com.ulfric.core.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

public class EnchantmentMagic extends PrisonEnchantment {

	private static final EnchantmentMagic INSTANCE = new EnchantmentMagic();
	public static EnchantmentMagic get() { return EnchantmentMagic.INSTANCE; }

	private EnchantmentMagic()
	{
		super(233, "MAGIC");
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel()
	{
		return 4;
	}

}