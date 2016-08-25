package com.ulfric.core.lwe;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandSchematicPaste extends Command {

	public CommandSchematicPaste(ModuleBase owner)
	{
		super("paste", owner);

		this.addArgument(Argument.builder().setPath("schem").addSimpleResolver(Schematic::valueOf).setUsage("worldedit.specify_schematic").build());
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();
		Schematic schematic = (Schematic) this.getObject("schem");

		ThreadUtils.runAsync(() -> schematic.paste(player.getLocation()));
	}

}