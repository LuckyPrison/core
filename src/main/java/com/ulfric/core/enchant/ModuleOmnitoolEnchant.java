package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;

final class ModuleOmnitoolEnchant extends Module {

	public ModuleOmnitoolEnchant()
	{
		super("omnitool-enchant", "Automatic tool switching", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onClick(PlayerInteractEvent event)
			{
				Block block = event.getBlock();

				if (block == null) return;

				if (!event.getAction().isLeftClick()) return;

				Material bestTool = block.getType().getBestTool();

				if (bestTool == null) return;

				Player player = event.getPlayer();

				ItemStack hand = player.getMainHand();

				if (hand == null || !hand.enchants().contains(EnchantmentOmnitool.INSTANCE)) return;

				hand.setType(bestTool);

				player.setMainHand(hand);
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		EnchantmentOmnitool.INSTANCE.register();
	}

	@Override
	public void onModuleDisable()
	{
		EnchantmentOmnitool.INSTANCE.unregister();
	}

}