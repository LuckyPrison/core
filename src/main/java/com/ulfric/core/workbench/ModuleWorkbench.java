package com.ulfric.core.workbench;

import com.ulfric.lib.coffee.module.Module;

public final class ModuleWorkbench extends Module {

	public ModuleWorkbench()
	{
		super("workbench", "Workbench command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandWorkbench(this));
	}

}
