package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandLeave extends GangCommand {

	public SubCommandLeave(ModuleBase owner)
	{
		super("leave", GangRank.MEMBER, owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "gangs.must_be_player");
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();
		UUID uuid = sender.getUniqueId();

		Gang gang = this.getGang();

		GangMember member = gang.getMember(uuid);

		if (member == null) return; // would be true if an admin did /gang leave <gangname>

		if (member.hasPermission(GangRank.LEADER))
		{
			if (gang.getMemberParticipants().size() != 1)
			{
				sender.sendLocalizedMessage("gangs.cannot_leave_leader");

				return;
			}

			Gangs gangs = Gangs.getInstance();

			String name = sender.getName();
			String gangName = gang.getName();

			sender.sendLocalizedMessage("gangs.left_disbanded", gangName);

			for (UUID enemyUUID : gang.getRelations(Relation.ENEMY))
			{
				Gang enemy = gangs.getGang(enemyUUID);

				for (Player enemyPlayer : enemy.getOnlinePlayers())
				{
					enemyPlayer.sendLocalizedMessage("gangs.disbanded_enemy", gangName, name);
				}
			}

			for (UUID allyUUID : gang.getRelations(Relation.ALLY))
			{
				Gang ally = gangs.getGang(allyUUID);

				for (Player allyPlayer : ally.getOnlinePlayers())
				{
					allyPlayer.sendLocalizedMessage("gangs.disbanded_ally", gangName, name);
				}
			}

			gangs.deleteGang(gang);

			return;
		}

		gang.removeMember(uuid);

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs.member_left", sender.getName()));

		sender.sendLocalizedMessage("gangs.left", gang.getName());

		new GangStatusEvent(sender, null).fire();
	}

}