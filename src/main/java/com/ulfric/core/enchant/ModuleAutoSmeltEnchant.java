package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemStack.EnchantList;

final class ModuleAutoSmeltEnchant extends Module {

	public ModuleAutoSmeltEnchant()
	{
		super("autosmelt-enchant", "Autosmelting ores", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onBreak(BlockBreakEvent event)
			{
				ItemStack hand = event.getHand();

				if (hand == null) return;

				EnchantList enchants = hand.enchants();

				int level = enchants.getLevel(EnchantmentAutoSmelt.INSTANCE);

				if (level <= 0) return;

				ItemStack item = event.getCustomItem();

				MaterialData smelt = item.getType().smelt();

				if (smelt == null) return;

				item.setType(smelt.getMaterial());
				item.setDurability(smelt.getData());
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		EnchantmentAutoSmelt.INSTANCE.register();
	}

	@Override
	public void onModuleDisable()
	{
		EnchantmentAutoSmelt.INSTANCE.unregister();
	}

}