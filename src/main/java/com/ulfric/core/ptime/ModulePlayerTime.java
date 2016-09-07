package com.ulfric.core.ptime;

import com.ulfric.lib.coffee.module.Module;

public final class ModulePlayerTime extends Module {

	public ModulePlayerTime()
	{
		super("playertime", "playertime command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandPlayerTime(this));
	}

}
