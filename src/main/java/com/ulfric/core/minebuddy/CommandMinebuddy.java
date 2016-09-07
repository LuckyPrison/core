package com.ulfric.core.minebuddy;

import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.module.ModuleBase;

final class CommandMinebuddy extends BaseCommand {

	CommandMinebuddy(ModuleBase owner)
	{
		super("minebuddy", owner, "mb");

		this.addCommand(new CommandInvite(owner));
		this.addCommand(new CommandAccept(owner));
		this.addCommand(new CommandLeave(owner));
	}

}