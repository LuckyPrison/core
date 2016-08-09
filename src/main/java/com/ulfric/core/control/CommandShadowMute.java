package com.ulfric.core.control;

import java.time.Instant;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandShadowMute extends TimedPunishmentBaseCommand {

	public CommandShadowMute(ModuleBase owner)
	{
		super("shadowmute", owner, "shadmute", "shamute", "smute");
	}

	@Override
	public void run()
	{
		Punisher punisher = this.getPunisher();
		Validate.notNull(punisher);

		PunishmentHolder holder = this.getHolder();
		Validate.notNull(holder);

		int[] referenced = this.getReferenced();
		Validate.notNull(referenced);

		Instant expiry = this.getExpiry();
		Validate.notNull(expiry);

		String reason = this.buildUnusedArgs();

		Punishment punishment = Punishments.newShadowMute(holder, punisher, reason, expiry, referenced);

		punishment.run();
	}

}