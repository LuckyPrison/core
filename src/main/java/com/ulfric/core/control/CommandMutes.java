package com.ulfric.core.control;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.coffee.module.ModuleBase;

class CommandMutes extends PunishmentListCommand {

	CommandMutes(ModuleBase owner)
	{
		super("mutes", owner, ImmutableList.of(PunishmentType.MUTE, PunishmentType.SHADOW_MUTE));
	}

}