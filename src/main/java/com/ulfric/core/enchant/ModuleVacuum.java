package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.event.Priority;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.world.World;
import com.ulfric.lib.craft.world.WorldUtils;

final class ModuleVacuum extends Module {

	public ModuleVacuum()
	{
		super("vacuum", "Makes items go into your inventory automagically", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		World defaultWorld = WorldUtils.getWorlds().get(0);
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true, priority = Priority.HIGH)
			public void onBreak(BlockBreakEvent event)
			{
				Player player = event.getPlayer();
				if (!player.getWorld().equals(defaultWorld)) return;

				ItemStack item = event.getCustomItem();

				if (item == null) return;

				event.doNotDrop();

				if (ListUtils.isEmpty(player.getInventory().addItem(item))) return;

				player.sendTitle(player.getLocalizedMessage("vacuum.inventory_full"), null, 5, 50, 5);
			}
		});
	}

}