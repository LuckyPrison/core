package com.ulfric.core.enchant;

import java.util.List;

import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

class ToolEnchantment extends Enchantment {

	protected ToolEnchantment(String name, int id, int max, List<Integer> conflicts)
	{
		super(name, id, max, conflicts, item -> item.getType().isTool());
	}

}