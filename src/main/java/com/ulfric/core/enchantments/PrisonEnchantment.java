package com.ulfric.core.enchantments;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.coffee.string.Named;

public abstract class PrisonEnchantment extends Enchantment implements Named {

	protected PrisonEnchantment(int id, String name)
	{
		super(id);
		this.name = name;
	}

	private final String name;

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean conflictsWith(Enchantment enchant)
	{
		return false;
	}

	@Override
	public int getStartLevel()
	{
		return 1;
	}

	@Override
	public boolean canEnchantItem(ItemStack item)
	{
		return this.getItemTarget().includes(item);
	}

}