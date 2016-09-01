package com.ulfric.core.modules;

import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.inventory.InventoryClickEvent;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.inventory.item.Material;

public final class ModuleStackSize extends Module {

	public ModuleStackSize()
	{
		super("stack-size", "Changes the max stack size of items", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		for (Material material : Material.getAllValues())
		{
			if (material.getMaxStackSize() != 64) continue;

			material.setMaxStackSize(120);
		}

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Player player = event.getPlayer();
				ThreadUtils.run(() -> player.getInventory().setMaxStackSize(120));
			}

			@Handler
			public void onClick(InventoryClickEvent event)
			{
				if (!event.getPlayer().getInventory().equals(event.getClicked())) return;

				ThreadUtils.run(event.getPlayer()::refreshInventory);
			}
		});
	}

}