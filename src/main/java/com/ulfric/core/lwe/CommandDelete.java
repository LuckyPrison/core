package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.region.Selection;
import com.ulfric.lib.coffee.region.Shape;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.block.MultiBlockChange;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.LocationUtils;
import com.ulfric.lib.craft.world.World;

class CommandDelete extends Command {

	CommandDelete(ModuleBase owner)
	{
		super("delete", owner);
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();
		MaterialData material = MaterialData.of(Material.of("AIR"));

		Selection selection = player.getSelection();

		if (!selection.isComplete())
		{
			player.sendLocalizedMessage("worldedit.selection_not_complete");

			return;
		}

		Shape shape = selection.toShape();		

		World world = player.getWorld();

		ThreadUtils.runAsync(() ->
		{
			int counter = 0;

			MultiBlockChange change = new MultiBlockChange(100);

			for (Vector vector : shape)
			{
				counter++;

				change.addBlock(LocationUtils.getLocation(world, vector), material);
			}

			change.run();

			player.sendLocalizedMessage("worldedit.setting_blocks", shape.getName(), counter);
		});
	}

}