package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.numbers.NumberUtils;

public class CommandLift extends Command {

	public CommandLift(ModuleBase owner)
	{
		super("lift", owner, "expire");

		this.addPermission("pardon.use");

		this.addArgument(Argument.builder().setPath("punishment").addSimpleResolver(str ->
		{
			String parse = str.startsWith("#") ? str.substring(1) : str;

			if (parse.isEmpty()) return null;

			Integer integer = NumberUtils.parseInteger(parse);

			if (integer == null) return null;

			return Punishments.getInstance().getPunishment(integer);
		}).setUsage("life.specify_punishment").build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Punishment punishment = (Punishment) this.getObject("punishment");

		if (!(punishment instanceof TimedPunishment))
		{
			sender.sendLocalizedMessage("lift.not_timed", punishment.getID(), punishment.getClass().getSimpleName());

			return;
		}

		TimedPunishment timed = (TimedPunishment) punishment;

		if (timed.isExpired())
		{
			sender.sendLocalizedMessage("lift.already_expired", punishment.getID());

			return;
		}

		Punisher updater = Punisher.valueOf(sender);

		String updateReason = this.buildUnusedArgs();

		timed.setExpiry(updater, updateReason, null);

		sender.sendLocalizedMessage("life.lifted", punishment.getID());
	}

}