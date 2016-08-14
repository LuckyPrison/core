package com.ulfric.core.gangs;

import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;

class RelationGangCommand extends GangCommand {

	public RelationGangCommand(String name, GangRank gangPermission, Relation relation, ModuleBase owner)
	{
		super(name, gangPermission, owner);

		Validate.notNull(relation);

		this.relation = relation;
	}

	private final Relation relation;

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		UUID uuid = sender.getUniqueId();
		Gang gang = this.getGang();

		GangMember member = gang.getMember(uuid);

		if (member != null && member.getGang().equals(gang))
		{
			sender.sendLocalizedMessage("gangs.relation_set_self", gang.getName());

			return;
		}

		Gangs gangs = Gangs.getInstance();

		member = gangs.getMember(uuid);

		Validate.notNull(member);

		Gang memberGang = member.getGang();

		memberGang.setRelation(gang.getUniqueId(), this.relation);

		String senderName = sender.getName();
		String gangName = memberGang.getName();

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs.relation_set_other", senderName, gangName));

		String otherGangName = gang.getName();

		memberGang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs.relation_set", senderName, otherGangName));
	}

}