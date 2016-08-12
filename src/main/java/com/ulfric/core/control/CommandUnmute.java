package com.ulfric.core.control;

import java.util.List;

import com.google.common.collect.Lists;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.string.StringUtils;

class CommandUnmute extends Command {

	public CommandUnmute(ModuleBase owner)
	{
		super("unmute", owner, "clearmutes", "clearmute");

		this.addPermission("unmute.use");

		this.addArgument(PunishmentHolder.ARGUMENT);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		PunishmentHolder holder = (PunishmentHolder) this.getObject(PunishmentHolder.ARGUMENT.getPath());

		List<Punishment> punishments1 = holder.getPunishments(PunishmentType.MUTE);
		List<Punishment> punishments2 = holder.getPunishments(PunishmentType.SHADOW_MUTE);
		List<Punishment> punishments = null;

		if (punishments1 != null)
		{
			punishments = punishments1;
		}

		if (punishments2 != null)
		{
			if (punishments == null)
			{
				punishments = punishments2;
			}
			else
			{
				punishments.addAll(punishments2);
			}
		}

		if (punishments == null || punishments.isEmpty())
		{
			sender.sendLocalizedMessage("pardon.no_mutes_found");

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
			sender.sendLocalizedMessage("unmute.no_active_mutes_found");

			return;
		}

		sender.sendLocalizedMessage("unmute.expired", StringUtils.mergeNicely(ids));
	}

}