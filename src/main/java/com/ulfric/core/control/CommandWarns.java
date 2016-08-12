package com.ulfric.core.control;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandWarns extends PunishmentListCommand {

	public CommandWarns(ModuleBase owner)
	{
		super("warns", owner, PunishmentType.WARN);
	}

}