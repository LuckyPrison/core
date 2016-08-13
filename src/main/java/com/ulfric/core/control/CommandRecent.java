package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.numbers.NumberUtils;

public class CommandRecent extends Command {

	public CommandRecent(ModuleBase owner)
	{
		super("recent", owner);

		this.addPermission("recent.use");

		this.addArgument(Argument.builder().setPath("count").setDefaultValue(-10).addSimpleResolver(str ->
		{
			Integer val = NumberUtils.parseInteger(str);

			if (val == null) return null;

			int value = Math.abs(val);

			if (value == 0) return null;

			value = Math.min(value, 100);

			return val;
		}).build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		Punishments punishments = Punishments.getInstance();

		int latest = punishments.currentID();

		if (latest == 0)
		{
			sender.sendLocalizedMessage("recent.none");

			return;
		}

		Locale locale = sender.getLocale();

		int count = (int) this.getObject("count");

		if (count > 0)
		{
			int diff = latest - count;

			if (diff < 0)
			{
				sender.sendMessage(locale.getFormattedMessage("recent.less", count, latest, diff));

				count -= diff;
			}
		}
		else
		{
			count = Math.abs(count);
		}

		int counter = 0;

		for (int x = latest; x > count; x--)
		{
			Punishment punishment = punishments.getPunishment(x);

			if (punishment == null) continue;

			counter++;

			sender.sendMessage(punishment.quickInspect(locale));
		}

		if (counter != count)
		{
			sender.sendMessage(locale.getFormattedMessage("recent.unable_to_find", count, counter));
		}

		sender.sendMessage(locale.getFormattedMessage("recent.footer", count));
	}

}