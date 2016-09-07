package com.ulfric.core.shutdown;

import com.ulfric.lib.coffee.module.Module;

public final class ModuleShutdown extends Module {

	public ModuleShutdown()
	{
		super("shutdown", "Shutdown command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandShutdown(this));
	}

}
