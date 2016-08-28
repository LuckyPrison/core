package com.ulfric.core.modules;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent.Action;
import com.ulfric.lib.craft.inventory.item.Material;

public final class ModuleTrample extends Module {

	public ModuleTrample()
	{
		super("trample", "blocks trampling crops", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		final Material soil = Material.of("SOIL");
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onInteract(PlayerInteractEvent event)
			{
				if (event.getAction() != Action.PHYSICAL) return;

				Block block = event.getBlock();

				if (block == null) return;

				if (!block.getType().equals(soil)) return;

				event.setCancelled(true);
			}
		});
	}

}