package com.ulfric.core.gangs;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.time.TimeUtils;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandCreate extends Command {

	public SubCommandCreate(ModuleBase owner)
	{
		super("create", owner, "new", "open");

		this.addArgument(Argument.builder().setPath("gang_name").addResolver(Resolvers.STRING).setUsage("gangs.create_specify_name").build());

		this.addEnforcer(Enforcers.IS_PLAYER, "gangs.create_must_be_player");
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();

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

		if (!StringUtils.isAlpha(name))
		{
			sender.sendLocalizedMessage("gangs-name-must-be-alpha");

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

		new GangStatusEvent(sender, null, gang).fire();
	}

}