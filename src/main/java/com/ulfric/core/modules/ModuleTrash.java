package com.ulfric.core.modules;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.SignListener;
import com.ulfric.lib.craft.event.inventory.InventoryClickEvent;
import com.ulfric.lib.craft.event.player.PlayerUseSignEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.panel.Button;
import com.ulfric.lib.craft.panel.Panel;

public class ModuleTrash extends Module {

	public ModuleTrash()
	{
		super("trash", "A module to help players throw items away", "1.0.0", "Packet");
	}

	ItemStack item;
	int size;

	@Override
	public void onModuleEnable()
	{
		Document document = this.getModuleConfig().getRoot();
		this.item = ItemUtils.getItem(document.getString("item", "ma.ironfence nac.&c&lEmpty<s>Trash"));
		this.size = NumberUtils.roundUp(document.getInteger("size", 45), 9);

		this.log("Setting trash size to " + this.size);
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new Command("trash", this)
		{
			@Override
			public void run()
			{
				CommandSender sender = this.getSender();

				if (!(sender instanceof Player)) return;

				ModuleTrash.this.openTrash((Player) sender);
			}
		});

		this.addListener(new SignListener(this, "trash", PlayerUseSignEvent.Action.RIGHT_CLICK)
		{
			@Override
			public void handle(Player player, Sign sign)
			{
				ModuleTrash.this.openTrash(player);
			}
		});
	}

	void openTrash(Player player)
	{
		Panel.createStandard(this.size, player.getLocalizedMessage("core.trash"))
			 .addButton(Button.builder()
					 		  .addSlot(this.size-1, ModuleTrash.this.item)
					 		  .addAction(this::emptyTrash)
					 		  .build())
			 .open(player);
	}

	private void emptyTrash(InventoryClickEvent event)
	{
		event.getInventory().empty(0, this.size-1);
		event.setCancelled(true);
	}

}