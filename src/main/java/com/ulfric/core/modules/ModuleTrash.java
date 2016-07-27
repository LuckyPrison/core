package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.SignListener;
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

	protected ItemStack item;

	@Override
	public void onModuleEnable()
	{
		this.item = ItemUtils.getItem(this.getModuleConfig().getRoot().getString("item", "ma.ironfence nac.&c&lEmpty<s>Trash"));
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
		Panel.createStandard(45, player.getLocalizedMessage("core.trash"))
			 .addButton(Button.builder()
					 		  .addSlot(44, ModuleTrash.this.item)
					 		  .addAction(event ->
					 		  {
					 			  event.getInventory().empty(0, 44);
					 			  event.setCancelled(true);
					 		  })
					 		  .build())
			 .open(player);
	}

}