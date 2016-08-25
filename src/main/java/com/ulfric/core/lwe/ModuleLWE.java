package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.region.Selection;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;

public class ModuleLWE extends Module {

	public ModuleLWE()
	{
		super("lwe", "LiteWorldEdit - a lightweight worldedit alternative", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandWorldEdit(this));

		Material woodenAxe = Material.of("WOODEN_AXE");
		this.addListener(new Listener(this)
		{
			@Handler
			public void onInteract(PlayerInteractEvent event)
			{
				Block block = event.getBlock();

				if (block == null) return;

				PlayerInteractEvent.Action action = event.getAction();

				if (action == PlayerInteractEvent.Action.PHYSICAL) return;

				Player player = event.getPlayer();

				ItemStack hand = player.getMainHand();

				if (hand == null) return;

				if (!woodenAxe.equals(hand.getType())) return;

				if (!player.hasPermission("worldedit.use")) return;

				Selection selection = player.getSelection();

				if (selection == null) return;

				if (action.isLeftClick())
				{
					selection.pushLeft(block.getLocation());

					player.sendLocalizedMessage("worldedit.selection_updated", "left");
				}

				else if (action.isRightClick())
				{
					selection.pushRight(block.getLocation());

					player.sendLocalizedMessage("worldedit.selection_updated", "right");
				}

				else
				{
					return;
				}

				event.setCancelled(true);
			}
		});
	}

}