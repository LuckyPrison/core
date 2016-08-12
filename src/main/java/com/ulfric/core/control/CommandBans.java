package com.ulfric.core.control;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandBans extends PunishmentListCommand {

	CommandBans(ModuleBase owner)
	{
		super("bans", owner, PunishmentType.BAN);
	}

}