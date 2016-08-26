package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandDisband extends GangCommand {

	public SubCommandDisband(ModuleBase owner)
	{
		super("disband", GangRank.LEADER, owner, "delete");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		String senderName = sender.getName();

		Gang gang = this.getGang();
		String gangName = gang.getName();

		Gangs gangs = Gangs.getInstance();

		for (Player player : gang.getOnlinePlayers())
		{
			new GangStatusEvent(player, gang, null).fire();
			player.sendLocalizedMessage("gangs.disbanded", gangName, senderName);
		}

		for (UUID enemyUUID : gang.getRelations(Relation.ENEMY))
		{
			Gang enemy = gangs.getGang(enemyUUID);

			for (Player enemyPlayer : enemy.getOnlinePlayers())
			{
				enemyPlayer.sendLocalizedMessage("gangs.disbanded_enemy", gangName, senderName);
			}
		}

		for (UUID allyUUID : gang.getRelations(Relation.ALLY))
		{
			Gang ally = gangs.getGang(allyUUID);

			for (Player allyPlayer : ally.getOnlinePlayers())
			{
				allyPlayer.sendLocalizedMessage("gangs.disbanded_ally", gangName, senderName);
			}
		}

		gangs.deleteGang(gang);

		sender.sendLocalizedMessage("gangs.disbanded_success", gangName, gang.getMemberParticipants().size());
	}

}