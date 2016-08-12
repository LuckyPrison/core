package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.command.Enforcers;
import com.ulfric.lib.coffee.module.ModuleBase;

public class SubCommandJoin extends GangCommand {

	public SubCommandJoin(ModuleBase owner)
	{
		super("join", owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "gangs.must_be_player");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		UUID uuid = sender.getUniqueId();

		Gangs gangs = Gangs.getInstance();

		GangMember membership = gangs.getMember(uuid);

		if (membership != null)
		{
			sender.sendLocalizedMessage("gangs.already_in_gang", membership.getGang().getName());

			return;
		}

		Gang gang = this.getGang();

		if (!gang.isInvited(sender.getUniqueId()) && !sender.hasPermission("gangs.admin"))
		{
			sender.sendLocalizedMessage("gangs.not_invited", gang.getName());

			return;
		}

		String name = sender.getName();

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs.joined_other", name));

		gang.addNewMember(uuid);

		sender.sendLocalizedMessage("gangs.joined", gang.getName());
	}

}