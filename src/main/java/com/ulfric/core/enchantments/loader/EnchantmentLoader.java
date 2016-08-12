package com.ulfric.core.enchantments.loader;

import java.util.Set;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentLoader {

	protected static IEnchantmentLoader impl = IEnchantmentLoader.EMPTY;

	public static <T extends Enchantment> Set<T> getEnchants(EnchantmentType type)
	{
		return EnchantmentLoader.impl.getEnchants(type);
	}


	protected interface IEnchantmentLoader
    {
        IEnchantmentLoader EMPTY = new IEnchantmentLoader() { };

        default <T extends Enchantment> Set<T> getEnchants(EnchantmentType type) { return null; }

    }

}