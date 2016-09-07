package com.ulfric.core.fly;

import com.ulfric.lib.coffee.module.Module;

public class ModuleFly extends Module {

	public ModuleFly()
	{
		super("fly", "Fly command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandFly(this));
	}

}
