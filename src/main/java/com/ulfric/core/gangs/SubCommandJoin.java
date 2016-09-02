package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandJoin extends GangCommand {

	public SubCommandJoin(ModuleBase owner)
	{
		super("join", null, owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "gangs-join-must-be-player");
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();
		UUID uuid = sender.getUniqueId();

		Gangs gangs = Gangs.getInstance();

		GangMember membership = gangs.getMember(uuid);

		if (membership != null)
		{
			sender.sendLocalizedMessage("gangs-already-in-gang", membership.getGang().getName());

			return;
		}

		Gang gang = this.getGang();

		if (!gang.isInvited(sender.getUniqueId()) && !sender.hasPermission("gangs.admin"))
		{
			sender.sendLocalizedMessage("gangs-not-invited", gang.getName());

			return;
		}

		String name = sender.getName();

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs-joined-other", name));

		gang.addNewMember(uuid);

		sender.sendLocalizedMessage("gangs-joined", gang.getName());

		new GangStatusEvent(sender, null, gang).fire();
	}

}