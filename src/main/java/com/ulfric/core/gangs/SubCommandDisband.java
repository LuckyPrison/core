package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandDisband extends GangCommand {

	public SubCommandDisband(ModuleBase owner)
	{
		super("disband", owner);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Gang gang = this.getGang();

		Gangs gangs = Gangs.getInstance();

		String gangName = gang.getName();
		String senderName = sender.getName();

		for (Player player : gang.getOnlinePlayers())
		{
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
			Gang enemy = gangs.getGang(allyUUID);

			for (Player allyPlayer : enemy.getOnlinePlayers())
			{
				allyPlayer.sendLocalizedMessage("gangs.disbanded_ally", gangName, senderName);
			}
		}

		gangs.deleteGang(gang);

		sender.sendLocalizedMessage("gangs.disbanded_success", gangName, gang.getMemberParticipants().size());
	}

}