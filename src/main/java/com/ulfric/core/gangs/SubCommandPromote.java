package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;

public class SubCommandPromote extends GangCommand {

	public SubCommandPromote(ModuleBase owner)
	{
		super("promote", GangRank.LIEUTENANT, owner);

		this.addArgument(OfflinePlayer.ARGUMENT);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		OfflinePlayer target = (OfflinePlayer) this.getObject("offline-player");

		UUID targetUUID = target.getUniqueId();

		if (targetUUID.equals(sender.getUniqueId()))
		{
			sender.sendLocalizedMessage("gangs.promote_self");

			return;
		}

		Gang gang = this.getGang();

		GangMember member = gang.getMember(targetUUID);

		if (member == null)
		{
			sender.sendLocalizedMessage("gangs.promote_not_member", gang.getName(), target.getName());

			return;
		}

		GangMember senderMember = gang.getMember(sender.getUniqueId());

		if (senderMember == null) return;

		if (member.hasPermission(senderMember.getRank()))
		{
			sender.sendLocalizedMessage("gangs.promote_already_ranked", target.getName(), EnumUtils.format(member.getRank()));

			return;
		}

		GangRank rank = member.getRank().nextRank();

		if (rank == null || rank == GangRank.LEADER)
		{
			sender.sendLocalizedMessage("gangs.promote_use_setrank");

			return;
		}

		gang.setRank(target.getUniqueId(), rank);

		String senderName = sender.getName();
		String rankName = EnumUtils.format(rank);
		String targetName = target.getName();

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs.promoted", senderName, targetName, rankName));
	}

}