package com.ulfric.core.gangs;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.ulfric.lib.coffee.command.ArgFunction;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.math.TimeUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandCreate extends Command {

	public SubCommandCreate(ModuleBase owner)
	{
		super("create", owner);

		this.addArgument(Argument.builder().setPath("gang_name").addResolver(ArgFunction.STRING_FUNCTION).setUsage("gangs.create_specify_name").build());

		this.addEnforcer(Player.class::isInstance, "gangs.create_must_be_player");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		Gangs gangs = Gangs.getInstance();

		GangMember member = gangs.getMember(sender.getUniqueId());

		if (member != null)
		{
			sender.sendLocalizedMessage("gangs.create_already_member", EnumUtils.format(member.getRank()), member.getGang().getName());

			return;
		}

		String name = (String) this.getObject("gang_name");

		int length = name.length();

		if (length < 3)
		{
			sender.sendLocalizedMessage("gangs.create_min_length", 3);

			return;
		}

		if (length > 10)
		{
			sender.sendLocalizedMessage("gangs.create_max_length", 10);

			return;
		}

		Gang gang = gangs.getGang(name);

		if (gang != null)
		{
			sender.sendLocalizedMessage("gangs.create_name_taken", gang.getName(), TimeUtils.formatMillis(TimeUtils.instantSince(gang.getCreated()).toEpochMilli(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS));

			return;
		}

		UUID gangUUID = UUID.randomUUID();

		gang = gangs.getGang(gangUUID);

		while (gang != null)
		{
			gang = gangs.getGang(gangUUID);
		}

		gang = Gang.newGang(gangs, gangUUID, name, sender.getUniqueId());

		sender.sendLocalizedMessage("gangs.created_gang", gang.getName());
	}

}