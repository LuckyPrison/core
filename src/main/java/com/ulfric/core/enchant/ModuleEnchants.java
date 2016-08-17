package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.module.Module;

public final class ModuleEnchants extends Module {

	public ModuleEnchants()
	{
		super("enchants", "Enchants module", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandEnchant(this));
	}

}