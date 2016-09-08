package com.ulfric.core.fireball;

import com.ulfric.lib.coffee.module.Module;

public final class ModuleFireball extends Module {

	public ModuleFireball()
	{
		super("fireball", "Fireball command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandFireball(this));
	}

}
