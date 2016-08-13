package com.ulfric.core.control;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.coffee.module.ModuleBase;

class CommandHistory extends PunishmentListCommand {

	public CommandHistory(ModuleBase owner)
	{
		super("history", owner, ImmutableList.copyOf(PunishmentType.values()));
	}

}