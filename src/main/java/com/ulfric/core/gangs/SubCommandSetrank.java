package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;

public class SubCommandSetrank extends GangCommand {

	public SubCommandSetrank(ModuleBase owner)
	{
		super("setrank", owner);

		this.addArgument(OfflinePlayer.ARGUMENT);
		this.addArgument(Argument.builder().setPath("rank").addSimpleResolver(GangRank::parseRank).setUsage("gangs.setrank_specify_rank").build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		OfflinePlayer target = (OfflinePlayer) this.getObject("offline-player");

		UUID targetUUID = target.getUniqueId();

		if (targetUUID.equals(sender.getUniqueId()))
		{
			sender.sendLocalizedMessage("gangs.setrank_self");

			return;
		}

		Gang gang = this.getGang();

		GangMember member = gang.getMember(targetUUID);

		if (member == null)
		{
			sender.sendLocalizedMessage("gangs.setrank_not_member", gang.getName(), target.getName());

			return;
		}

		GangMember senderMember = gang.getMember(sender.getUniqueId());

		if (senderMember == null) return;

		if (member.hasPermission(senderMember.getRank()))
		{
			sender.sendLocalizedMessage("gangs.setrank_already_ranked", target.getName(), EnumUtils.format(member.getRank()));

			return;
		}

		GangRank rank = (GangRank) this.getObject("rank");

		if (!senderMember.isAbove(rank))
		{
			if (rank != GangRank.LEADER)
			{
				sender.sendLocalizedMessage("gangs.setrank_no_permission");

				return;
			}

			if (!senderMember.hasPermission(rank))
			{
				sender.sendLocalizedMessage("gangs.setrank_no_permission");

				return;
			}

			gang.setRank(sender.getUniqueId(), GangRank.LIEUTENANT);
		}

		gang.setRank(target.getUniqueId(), rank);

		String senderName = sender.getName();
		String rankName = EnumUtils.format(rank);
		String targetName = target.getName();

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs.ranked", senderName, targetName, rankName));
	}

}