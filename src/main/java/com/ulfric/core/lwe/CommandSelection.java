package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.region.Shapes;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandSelection extends Command {

	public CommandSelection(ModuleBase owner)
	{
		super("selection", owner, "select", "sel");

		this.addArgument(Argument.builder().setPath("shape").addSimpleResolver(str -> EnumUtils.valueOf(str, Shapes.class, 4)).setUsage("worldedit-selection-specify-type").build());
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		Shapes shape = (Shapes) this.getObject("shape");

		player.setSelection(shape.newSelection());

		player.sendLocalizedMessage("worldedit-selection-type-set", player.getSelection().getName());
	}

}
