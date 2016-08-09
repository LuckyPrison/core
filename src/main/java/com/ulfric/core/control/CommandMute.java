package com.ulfric.core.control;

import java.time.Instant;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandMute extends TimedPunishmentBaseCommand {

	public CommandMute(ModuleBase owner)
	{
		super("mute", owner);
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

		Punishments punishments = Punishments.getInstance();

		int id = punishments.getAndIncrementCounter();

		Punishment punishment = Punishments.newMute(id, holder, punisher, reason, expiry, referenced, silent);

		punishment.run();
	}

}