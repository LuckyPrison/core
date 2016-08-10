package com.ulfric.core.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;

public class StateEnchantment extends PrisonEnchantment {

    public static IStateEnchantmentImpl impl = IStateEnchantmentImpl.EMPTY;

    protected StateEnchantment(int id, String name)
    {
        super(id, name);
    }

    @Override
    public int getMaxLevel()
    {
        return 0;
    }

    @Override
    public EnchantmentTarget getItemTarget()
    {
        return null;
    }

   public interface IStateEnchantmentImpl {

       IStateEnchantmentImpl EMPTY = new IStateEnchantmentImpl() {};

       default boolean shouldAct(Player player) {
           return false;
       }
   }
}
