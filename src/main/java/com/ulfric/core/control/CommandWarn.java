package com.ulfric.core.control;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandWarn extends TimedPunishmentBaseCommand {

	public CommandWarn(ModuleBase owner)
	{
		super("warn", owner, "wa");
	}

	@Override
	public void run()
	{
		String reason = this.buildUnusedArgs();

		if (StringUtils.isBlank(reason))
		{
			this.getSender().sendLocalizedMessage("warn.must_specify_reason");

			return;
		}

		Punisher punisher = this.getPunisher();
		Validate.notNull(punisher);

		PunishmentHolder holder = this.getHolder();
		Validate.notNull(holder);

		int[] referenced = this.getReferenced();
		Validate.notNull(referenced);

		Instant expiry = this.getExpiry();
		Validate.notNull(expiry);

		Punishment punishment = Punishments.newWarn(holder, punisher, reason, expiry, referenced);

		punishment.run();
	}

}