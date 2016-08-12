package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;

public class SubCommandInfo extends GangCommand {

	public SubCommandInfo(ModuleBase owner)
	{
		super("info", owner);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Gang gang = this.getGang();

		Locale locale = sender.getLocale();

		StringBuilder builder = new StringBuilder();

		builder.append(locale.getFormattedMessage("gangs.info_header", gang.getName()));
		// TODO more info, also allow verbose info

		sender.sendLocalizedMessage(builder.toString());
	}

}