package com.ulfric.core.backpack;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.Inventory;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.panel.Button;
import com.ulfric.lib.craft.panel.Panel;
import com.ulfric.lib.craft.panel.standard.StandardPanel;

import java.util.stream.IntStream;

public class BackpackPage {

	private static final int INVENTORY_SIZE = 45;

	private static final ItemStack BACK_BUTTON;
	private static final ItemStack FORWARD_BUTTON;

	static
	{
		BACK_BUTTON = ItemUtils.getItem(Material.of("ARROW"));

		FORWARD_BUTTON = ItemUtils.getItem(Material.of("ARROW"));
	}

	private final ModuleBackpack base;
	private final Backpack backpack;
	private final Player viewer;
	private int currentPage;

	public BackpackPage(ModuleBackpack base, Backpack backpack, Player viewer, int currentPage)
	{
		this.base = base;
		this.backpack = backpack;
		this.viewer = viewer;
		this.currentPage = currentPage;
	}

	public void open()
	{
		StandardPanel panel = Panel.createStandard(
				INVENTORY_SIZE - (!(this.canPageDown() && this.canPageUp()) ? 9 : 0),
				this.viewer.getLocalizedMessage("backpacks.title",
				this.backpack.getOwner().getName(),
				this.currentPage)
		);

		Inventory contents = this.backpack.getContents(this.currentPage);

		IntStream.range(0, INVENTORY_SIZE - 9).forEach(slot ->
		{
			ItemStack item = contents.getItem(slot);

			if (item != null)
			{
				panel.setItem(slot, item);
			}

		});

		if (this.canPageDown())
		{
			ItemStack back = BACK_BUTTON.copy();

			ItemMeta meta = back.getMeta();

			meta.setDisplayName(this.viewer.getLocalizedMessage("backpacks.prev"));

			back.setMeta(meta);

			panel.addButton(
					Button.builder()
							.addSlot(INVENTORY_SIZE - 9, back)
							.addAction(event ->
							{
								this.pageDown();
								new BackpackPage(this.base, this.backpack, this.viewer, this.currentPage).open();
							})
							.build()
			);

			panel.setItem(INVENTORY_SIZE - 9, back);
		}

		if (this.canPageUp())
		{
			ItemStack forward = FORWARD_BUTTON.copy();

			ItemMeta meta = forward.getMeta();

			meta.setDisplayName(this.viewer.getLocalizedMessage("backpacks.forward"));

			forward.setMeta(meta);

			panel.addButton(
					Button.builder()
							.addSlot(INVENTORY_SIZE - 1, forward)
							.addAction(event ->
							{
								this.pageUp();
								new BackpackPage(this.base, this.backpack, this.viewer, this.currentPage).open();
							})
							.build()
			);

			panel.setItem(INVENTORY_SIZE - 1, forward);
		}

		panel.withAllowEmptySlotClicks(true);

		panel.withDefaultCancel(false);

		panel.withClickConsumer(event ->
		{
			if (event.getSlot() >= INVENTORY_SIZE - 9)
			{
				event.setCancelled(true);
			}
			else
			{
				this.backpack.updatePage(this.currentPage, contents);
			}
		});

		panel.withCloseConsumer(event -> this.backpack.save());

	}

	private boolean canPageUp()
	{
		return this.currentPage < this.backpack.getMaxPage();
	}

	private boolean canPageDown()
	{
		return this.currentPage > 1;
	}

	private void pageUp()
	{
		if (this.canPageUp())
		{
			this.currentPage++;
		}
	}

	private void pageDown()
	{
		if (this.canPageDown())
		{
			this.currentPage--;
		}
	}

}
