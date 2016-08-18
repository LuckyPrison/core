package com.ulfric.core.mines;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;

public class CommandMineReset extends Command {

	public CommandMineReset(ModuleBase owner)
	{
		super("minereset", owner, "mr");

		this.addArgument(Argument.builder().setPath("mine").addSimpleResolver(Mines.INSTANCE::getByName).build());

		this.addPermission("minereset.use");
	}

	@Override
	public void run()
	{
		Mine mine = (Mine) this.getObject("mine");

		mine.reset();
	}

}