package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.module.ModuleBase;

public class CommandGangs extends Command {

	public CommandGangs(ModuleBase owner)
	{
		super("gangs", owner, "gang", "g", "party", "clan");

		Command command = new SubCommandInfo(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("i").add("inspect").add("check").build());
	}

	@Override
	public void run()
	{
		// TODO info command
	}

}