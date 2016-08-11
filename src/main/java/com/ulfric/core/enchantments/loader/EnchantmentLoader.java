package com.ulfric.core.enchantments.loader;

import org.bukkit.enchantments.Enchantment;

import java.util.Set;

public class EnchantmentLoader {

    protected static IEnchantmentLoader impl = IEnchantmentLoader.EMPTY;

    public static <T extends Enchantment> Set<T> getEnchants(EnchantmentType type)
    {
        return EnchantmentLoader.impl.getEnchants(type);
    }


    protected interface IEnchantmentLoader {
        IEnchantmentLoader EMPTY = new IEnchantmentLoader() {};

        default <T extends Enchantment> Set<T> getEnchants(EnchantmentType type)
        {
            return null;
        }

    }

}