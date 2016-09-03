package com.ulfric.core.modules;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.event.Priority;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.inventory.InventoryClickEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.panel.anvil.AnvilInventory;
import com.ulfric.lib.craft.panel.anvil.AnvilSlot;
import com.ulfric.lib.craft.string.ChatUtils;

public class ModuleRenameFix extends Module {

	public ModuleRenameFix()
	{
		super("rename-fix", "Fixes rename coloring", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new RenameListener());
	}

	private class RenameListener extends Listener {

		public RenameListener()
		{
			super(ModuleRenameFix.this);
		}

		@Handler(priority = Priority.HIGHEST)
		public void on(InventoryClickEvent event)
		{
			Player player = event.getPlayer();

			if (player.hasBreadcrumb())
			{
				return;
			}

			if (event.getInventory() instanceof AnvilInventory)
			{
				AnvilInventory inventory = (AnvilInventory) event.getInventory();

				if (event.getClicked() != inventory)
				{
					return;
				}

				if (event.getSlot() != AnvilSlot.OUTPUT.getSlot())
				{
					return;
				}

				ItemStack item = inventory.getItem(AnvilSlot.OUTPUT);

				if (item.getMeta() != null)
				{
					ItemMeta meta = item.getMeta();

					meta.setDisplayName(
							ChatUtils.stripColor(
									ChatUtils.color(meta.getDisplayName())
							)
					);
				}
			}
		}

	}

}
