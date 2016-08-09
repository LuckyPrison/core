package com.ulfric.core.control;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;

class CommandWarns extends Command {

	public CommandWarns(ModuleBase owner)
	{
		super("warns", owner, "listwarns", "lwarn");

		this.addArgument(PunishmentHolder.ARGUMENT);

		this.addPermission("control.base");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		PunishmentHolder holder = (PunishmentHolder) this.getObject(PunishmentHolder.ARGUMENT.getPath());
		String name = holder.getName(sender);

		Punishments cache = Punishments.getInstance();
		List<Punishment> punishments = holder.getPunishments(PunishmentType.WARN);

		if (punishments == null)
		{
			punishments = Lists.newArrayList();
		}

		if (holder.hasIP())
		{
			punishments.addAll(cache.getHolder(holder.getUniqueId()).getPunishments(PunishmentType.WARN));
		}

		if (punishments.isEmpty())
		{
			sender.sendLocalizedMessage("warns.none", name);

			return;
		}

		Iterator<Punishment> iterator = punishments.iterator();

		while (iterator.hasNext())
		{
			Punishment next = iterator.next();

			if (!(next instanceof Warn))
			{
				iterator.remove();

				continue;
			}

			Warn warn = (Warn) next;

			if (warn.isNotExpired()) continue;

			iterator.remove();
		}

		if (punishments.isEmpty())
		{
			sender.sendLocalizedMessage("warns.none", name);

			return;
		}

		int size = punishments.size();

		Locale locale = sender.getLocale();

		if (size == 1)
		{
			sender.sendMessage(locale.getFormattedMessage("warns.single", name));
		}
		else
		{
			sender.sendMessage(locale.getFormattedMessage("warns.multiple", size, name));
		}

		String raw = locale.getRawMessage("warns.entry");

		for (Punishment punishment : punishments)
		{
			sender.sendMessage(raw, punishment.getID(), punishment.getPunisher().getName(sender), punishment.getReason());
		}
	}

}