package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.region.Selection;
import com.ulfric.lib.coffee.region.Shape;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.block.MultiBlockChange;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.location.LocationUtils;
import com.ulfric.lib.craft.world.World;

class CommandSet extends Command {

	CommandSet(ModuleBase owner)
	{
		super("set", owner);

		this.addArgument(Argument.builder().setPath("material").addSimpleResolver(MaterialData::of).setUsage("worldedit.specify_set_type").build());
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();
		MaterialData material = (MaterialData) this.getObject("material");

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