package com.ulfric.core.control;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandKills extends PunishmentListCommand {

	CommandKills(ModuleBase owner)
	{
		super("kills", owner, PunishmentType.KILL);
	}

}