package com.ulfric.core.echest;

import com.ulfric.lib.coffee.module.Module;

public final class ModuleEnderChest extends Module {

	public ModuleEnderChest()
	{
		super("ender-chest", "Enderchest command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandEnderChest(this));
	}

}
