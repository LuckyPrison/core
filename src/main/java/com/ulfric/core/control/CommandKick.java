package com.ulfric.core.control;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.module.ModuleBase;

class CommandKick extends PunishmentBaseCommand {

	public CommandKick(ModuleBase owner)
	{
		super("kick", owner);
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

		Punishments punishments = Punishments.getInstance();

		int id = punishments.getAndIncrementCounter();

		Punishment punishment = Punishments.newKick(id, holder, punisher, reason, referenced, silent);

		punishment.run();
	}

}