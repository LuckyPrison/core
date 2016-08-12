package com.ulfric.core.control;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandCommandMutes extends PunishmentListCommand {

	CommandCommandMutes(ModuleBase owner)
	{
		super("commandmutes", owner, PunishmentType.COMMAND_MUTE, "cmdmute", "cmute");
	}

}