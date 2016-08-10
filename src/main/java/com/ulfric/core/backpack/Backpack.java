package com.ulfric.core.backpack;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.inventory.InventoryClickEvent;
import com.ulfric.lib.craft.inventory.Inventory;
import com.ulfric.lib.craft.inventory.InventoryUtils;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.panel.Button;
import com.ulfric.lib.craft.panel.Panel;

class Backpack extends Panel {
	private final static ItemStack NEXT = setName(ItemUtils.getItem(Material.ARROW), "Next Page");
	private final static ItemStack PREV = setName(ItemUtils.getItem(Material.ARROW), "Previous Page");
	// TODO: Add these items to fill the bottom row?
	private final static ItemStack FILL = setName(ItemUtils.getItem(Material.EGG), "Filler");

	private final Player player;
	private final OfflinePlayer target;
	private final Inventory inventory;
	private final int maxPages;
	private int page;

	Backpack(Player player, OfflinePlayer other, int initialPage, int maxPages)
	{
		this.player = player;
		this.target = other == null ? player : other;
		this.maxPages = Math.max(maxPages, 1);
		page = Math.min(Math.max(initialPage, 1), this.maxPages); // Lock page between 1 < page < max
		inventory = InventoryUtils.newInventory(45, player.getLocalizedMessage("core.backpack.title"));

		// TODO: Store local buttons instead?
		this.buttons.add(Button.builder().addSlot(36, PREV).addAction(e -> this.pageDown()).build());
		this.buttons.add(Button.builder().addSlot(44, NEXT).addAction(e -> this.pageUp()).build());
	}

	private boolean canPageUp()
	{
		return page < maxPages;
	}

	private boolean canPageDown()
	{
		return page > 1;
	}

	private void pageUp()
	{
		if (this.canPageUp())
		{
			// Save current page
			this.savePage();
			page++;
			// Render the new page
			this.render(player);
		}
	}

	private void pageDown()
	{
		if (this.canPageDown())
		{
			// Save current page
			this.savePage();
			page--;
			// Render the new page
			this.render(player);
		}
	}

	@Override
	protected void onInventoryClick(InventoryClickEvent event)
	{
		// Check if we're clicking the last row of the backpack
		if (event.getSlot() < this.inventory.getSize() - 9)
		{
			return;
		}
		// Don't allow modifying the last row of items
		event.setCancelled(true);
		// The rest is handled by the buttons
	}

	@Override
	protected void onInventoryClose()
	{
		this.savePage();
	}

	@Override
	protected boolean render(Player player)
	{
		Map<Integer, ItemStack> pack = Maps.newHashMap(); // TODO: Get backpack items
		pack.forEach(this::addItem);

		if (this.canPageDown())
		{
			// Add prev button
			addButton(this.buttons.get(0), player.getLocalizedMessage("core.backpack.prev"), this.page);
		}
		if (this.canPageUp())
		{
			// Add next button
			addButton(this.buttons.get(1), player.getLocalizedMessage("core.backpack.next"), this.page + 2);
		}
		player.openInventory(this.inventory);
		return true;
	}

	private void addButton(Button button, String lore, int page)
	{
		button.getSlots().forEach((i, stack) -> addItem(i, updateItem(stack.copy(), lore, page)));
	}

	private void addItem(int slot, ItemStack stack)
	{
		this.inventory.setItem(slot, stack);
	}

	private void savePage()
	{
		// Save backpack data
		Map<Integer, ItemStack> items = new HashMap<>();
		for (int i = 0; i < this.inventory.getSize() - 9; i++)
		{
			ItemStack item = this.inventory.getItem(i);
			if (item != null && item.getType() != com.ulfric.lib.craft.inventory.item.Material.of("AIR"))
			{
				items.put(i, item);
			}
		}
		// TODO: save data
	}

	private static ItemStack updateItem(ItemStack item, String lore, int page)
	{
		ItemMeta meta = item.getMeta();
		meta.setPluginLore(Lists.newArrayList(String.format(lore, page)));
		item.setAmount(page);
		item.setMeta(meta);
		return item;
	}

	private static ItemStack setName(ItemStack item, String name)
	{
		ItemMeta meta = item.getMeta();
		meta.setDisplayName(name);
		item.setMeta(meta);
		return item;
	}
}
