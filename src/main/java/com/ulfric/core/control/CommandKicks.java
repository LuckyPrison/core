package com.ulfric.core.control;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandKicks extends PunishmentListCommand {

	CommandKicks(ModuleBase owner)
	{
		super("kicks", owner, PunishmentType.KICK);
	}

}