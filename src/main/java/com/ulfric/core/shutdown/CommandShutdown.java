package com.ulfric.core.shutdown;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.server.ServerUtils;

final class CommandShutdown extends Command {

	public CommandShutdown(ModuleShutdown owner)
	{
		super("shutdown", owner);

		this.addPermission("shutdown.use");
	}

	@Override
	public void run()
	{
		this.getSender().sendLocalizedMessage("shutdown-shutting-down");

		// Maybe some sort of broadcast? Maybe an optional argument for a broadcast, countdown, etc.?

		ServerUtils.shutdown();
	}

}
