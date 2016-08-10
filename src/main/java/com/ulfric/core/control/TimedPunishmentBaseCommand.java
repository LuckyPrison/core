package com.ulfric.core.control;

import java.time.Instant;

import com.ulfric.lib.coffee.module.ModuleBase;

abstract class TimedPunishmentBaseCommand extends PunishmentBaseCommand {

	public TimedPunishmentBaseCommand(String name, ModuleBase owner)
	{
		super(name, owner);

		this.addArgument(TimedPunishment.TIME_ARGUMENT);
	}

	public TimedPunishmentBaseCommand(String name, ModuleBase owner, String... dynamicAliases)
	{
		super(name, owner, dynamicAliases);

		this.addArgument(TimedPunishment.TIME_ARGUMENT);
	}

	public final Instant getExpiry()
	{
		return (Instant) this.getObject(TimedPunishment.TIME_ARGUMENT.getPath());
	}

}