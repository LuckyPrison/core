package com.ulfric.core.control;

import java.util.List;

import com.google.common.collect.Lists;
import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.string.StringUtils;

class CommandUncommandmute extends Command {

	public CommandUncommandmute(ModuleBase owner)
	{
		super("uncommandmute", owner, "uncmdmute", "uncmute");

		this.addPermission("uncommandmute.use");

		this.addArgument(PunishmentHolder.ARGUMENT);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		PunishmentHolder holder = (PunishmentHolder) this.getObject(PunishmentHolder.ARGUMENT.getPath());

		List<Punishment> punishments = holder.getPunishments(PunishmentType.COMMAND_MUTE);

		if (ListUtils.isEmpty(punishments))
		{
			sender.sendLocalizedMessage("uncommandmute.no_bans_found");

			return;
		}

		Punisher updater = Punisher.valueOf(sender);

		List<Integer> ids = Lists.newArrayList();

		String updateReason = this.buildUnusedArgs();

		for (Punishment punishment : punishments)
		{
			if (!(punishment instanceof TimedPunishment)) continue;

			TimedPunishment ban = (TimedPunishment) punishment;

			if (ban.isExpired()) continue;

			ban.setExpiry(updater, updateReason, null);

			ids.add(punishment.getID());
		}

		if (ids.isEmpty())
		{
			sender.sendLocalizedMessage("pardon.no_active_commandmutes_found");

			return;
		}

		sender.sendLocalizedMessage("uncommandmute.expired", StringUtils.mergeNicely(ids));
	}

}