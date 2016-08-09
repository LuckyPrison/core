package com.ulfric.core.control;

import java.time.Instant;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandCommandMute extends TimedPunishmentBaseCommand {

	public CommandCommandMute(ModuleBase owner)
	{
		super("commandmute", owner, "commmute", "commute", "comute", "cmdmute");
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

		boolean silent = this.isSilent();

		Instant expiry = this.getExpiry();
		Validate.notNull(expiry);

		String reason = this.buildUnusedArgs();

		Punishment punishment = Punishments.newCommandMute(holder, punisher, reason, expiry, referenced, silent);

		punishment.run();
	}

}