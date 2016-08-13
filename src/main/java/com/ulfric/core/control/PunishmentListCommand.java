package com.ulfric.core.control;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.numbers.NumberUtils;

class PunishmentListCommand extends Command {

	private static final String[] aliases(String... aliases)
	{
		int length = aliases.length;

		if (length == 0) return aliases;

		List<String> list = Lists.newArrayListWithCapacity(length * 5);

		for (String alias : aliases)
		{
			list.add(alias + 's');
			list.add("list" + alias);
			list.add("list" + alias + 's');
			list.add('l' + alias);
			list.add('l' + alias + "s");
		}

		return list.toArray(new String[list.size()]);
	}

	protected PunishmentListCommand(String name, ModuleBase owner, PunishmentType type, String... aliases)
	{
		this(name, owner, ImmutableList.of(type), aliases);
	}

	protected PunishmentListCommand(String name, ModuleBase owner, List<PunishmentType> types, String... aliases)
	{
		super(name, owner, PunishmentListCommand.aliases(aliases));

		this.types = types;

		this.addArgument(PunishmentHolder.ARGUMENT);

		this.addArgument(Argument.builder().setPath("count").setDefaultValue(-10).addSimpleResolver(str ->
		{
			Integer val = NumberUtils.parseInteger(str);

			if (val == null) return null;

			int value = Math.abs(val);

			if (value == 0) return null;

			value = Math.min(value, 100);

			return val;
		}).build());

		this.addPermission("control.base");
	}

	private final List<PunishmentType> types;

	@Override
	public final void run()
	{
		CommandSender sender = this.getSender();

		PunishmentHolder holder = (PunishmentHolder) this.getObject(PunishmentHolder.ARGUMENT.getPath());
		String name = holder.getName(sender);

		Punishments cache = Punishments.getInstance();

		List<Punishment> punishments = Lists.newArrayList();

		for (PunishmentType type : this.types)
		{
			List<Punishment> found = holder.getPunishments(type);

			if (found != null)
			{
				punishments.addAll(found);
			}

			if (holder.hasIP())
			{
				found = cache.getHolder(holder.getIP()).getPunishments(type);

				if (found == null) continue;

				punishments.addAll(found);
			}
		}

		if (punishments.isEmpty())
		{
			sender.sendLocalizedMessage(this.getName() + ".none", name);

			return;
		}

		Iterator<Punishment> iterator = punishments.iterator();

		while (iterator.hasNext())
		{
			Punishment next = iterator.next();

			if (!(next instanceof TimedPunishment)) continue;

			TimedPunishment timed = (TimedPunishment) next;

			if (timed.isNotExpired()) continue;

			iterator.remove();
		}

		if (punishments.isEmpty())
		{
			sender.sendLocalizedMessage(this.getName() + ".none", name);

			return;
		}

		int size = punishments.size();

		Locale locale = sender.getLocale();

		if (size == 1)
		{
			sender.sendMessage(locale.getFormattedMessage(this.getName() + ".single", name));
		}
		else
		{
			sender.sendMessage(locale.getFormattedMessage(this.getName() + ".multiple", size, name));
		}

		String raw = locale.getRawMessage(this.getName() + ".entry");

		int count = (int) this.getObject("count");

		int startFrom = Math.max(0, size - count);

		for (int x = startFrom; x > size; x++)
		{
			Punishment punishment = punishments.get(x);

			sender.sendMessage(raw, punishment.getID(), punishment.getPunisher().getName(sender), punishment.getReason());
		}
	}

}