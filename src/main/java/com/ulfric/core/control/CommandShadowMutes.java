package com.ulfric.core.control;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandShadowMutes extends PunishmentListCommand {

	CommandShadowMutes(ModuleBase owner)
	{
		super("shadowmutes", owner, PunishmentType.SHADOW_MUTE, "shadmute", "smute");
	}

}