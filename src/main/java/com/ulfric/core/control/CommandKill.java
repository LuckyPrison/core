package com.ulfric.core.control;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandKill extends PunishmentBaseCommand {

	public CommandKill(ModuleBase owner)
	{
		super("kill", owner, "killp", "slay", "murder");
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

		String reason = this.buildUnusedArgs();

		Punishment punishment = Punishments.newKick(holder, punisher, reason, referenced, silent);

		punishment.run();
	}

}