package com.ulfric.core.echest;

import com.ulfric.lib.coffee.module.Module;

public final class ModuleEnderchest extends Module {

	public ModuleEnderchest()
	{
		super("ender-chest", "Enderchest command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandEnderchest(this));
	}

}
