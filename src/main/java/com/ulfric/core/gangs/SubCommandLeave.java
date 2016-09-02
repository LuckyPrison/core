package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandLeave extends GangCommand {

	public SubCommandLeave(ModuleBase owner)
	{
		super("leave", GangRank.MEMBER, owner, "quit");

		this.addEnforcer(Enforcers.IS_PLAYER, "gangs-leave-must-be-player");
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
				sender.sendLocalizedMessage("gangs-cannot-leave-leader");

				return;
			}

			Gangs gangs = Gangs.getInstance();

			String name = sender.getName();
			String gangName = gang.getName();

			sender.sendLocalizedMessage("gangs-left-disbanded", gangName);

			for (UUID enemyUUID : gang.getRelations(Relation.ENEMY))
			{
				Gang enemy = gangs.getGang(enemyUUID);

				for (Player enemyPlayer : enemy.getOnlinePlayers())
				{
					enemyPlayer.sendLocalizedMessage("gangs-disbanded-enemy", gangName, name);
				}
			}

			for (UUID allyUUID : gang.getRelations(Relation.ALLY))
			{
				Gang ally = gangs.getGang(allyUUID);

				for (Player allyPlayer : ally.getOnlinePlayers())
				{
					allyPlayer.sendLocalizedMessage("gangs-disbanded-ally", gangName, name);
				}
			}

			gangs.deleteGang(gang);

			return;
		}

		gang.removeMember(uuid);

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs-member-left", sender.getName()));

		sender.sendLocalizedMessage("gangs-left", gang.getName());

		new GangStatusEvent(sender, gang, null).fire();
	}

}