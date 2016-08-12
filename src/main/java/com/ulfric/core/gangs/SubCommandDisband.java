package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
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
		String senderName = sender.getName();

		Gang gang = this.getGang();
		String gangName = gang.getName();

		GangMember member = gang.getMember(sender.getUniqueId());

		if (member != null && member.getRank() != GangRank.LEADER && !sender.hasPermission("gangs.admin"))
		{
			sender.sendLocalizedMessage("gangs.must_be_leader");

			for (GangMember leader : gang.getMembersByRank(GangRank.LEADER))
			{
				OfflinePlayer player = leader.toOfflinePlayer();
				Player onlinePlayer = player.toPlayer();

				Locale locale = null;

				if (onlinePlayer != null)
				{
					locale = onlinePlayer.getLocale();

					onlinePlayer.sendMessage(locale.getFormattedMessage("gangs.attempted_delete", senderName));
				}
				else
				{
					locale = Locale.getDefault();
				}

				player.sendEmail("LuckyPrison Gang Attempted Deletion", locale.getFormattedMessage("gangs.attempted_delete_email", gangName, senderName));
			}

			return;
		}

		Gangs gangs = Gangs.getInstance();

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