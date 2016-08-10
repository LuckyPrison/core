package com.ulfric.core.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

/**
 * Programmed by Tevin on 7/19/2016.
 */
public class EnchantmentAutoSell extends StateEnchantment {

    private static EnchantmentAutoSell i;

    public static EnchantmentAutoSell get() {
        if (i == null) {
            i = new EnchantmentAutoSell();
        }
        return i;
    }

    protected EnchantmentAutoSell()
    {
        super(204, "AUTOSELL");
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public EnchantmentTarget getItemTarget()
    {
        return EnchantmentTarget.TOOL;
    }
}
