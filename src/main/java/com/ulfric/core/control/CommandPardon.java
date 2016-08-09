package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;

class CommandPardon extends Command {

	public CommandPardon(ModuleBase owner)
	{
		super("pardon", owner, "unban");
	}

	@Override
	public void run()
	{
		
	}

}