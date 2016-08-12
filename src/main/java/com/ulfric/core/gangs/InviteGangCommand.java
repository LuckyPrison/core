package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;

abstract class InviteGangCommand extends GangCommand {

	public InviteGangCommand(String name, ModuleBase owner)
	{
		super(name, owner);

		this.addArgument(OfflinePlayer.ARGUMENT);

		this.addEnforcer(sender ->
		{
			if (sender.hasPermission("gangs.admin")) return true;

			GangMember member = Gangs.getInstance().getMember(sender.getUniqueId());

			return member.hasPermission(GangRank.OFFICER);
		}, "gangs.manage_invite_must_be_officer");
	}

}