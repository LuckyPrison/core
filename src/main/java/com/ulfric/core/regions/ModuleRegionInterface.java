package com.ulfric.core.regions;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent.Action;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.region.RegionColl;

public class ModuleRegionInterface extends Module {

	public ModuleRegionInterface()
	{
		super("region-interface", "Regions interface", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandRegions(this));

		Material leather = Material.of("LEATHER");

		this.addListener(new Listener(this)
		{
			@Handler
			public void onClick(PlayerInteractEvent event)
			{
				if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

				Block block = event.getBlock();

				if (block == null) return;

				Player player = event.getPlayer();

				ItemStack main = player.getMainHand();

				if (main == null) return;

				if (main.getType() != leather) return;

				if (!player.hasPermission("regions.list")) return;

				player.sendMessage(RegionColl.at(block.getLocation()).toString());
			}
		});
	}

}