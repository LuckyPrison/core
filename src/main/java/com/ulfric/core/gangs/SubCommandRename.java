package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.ArgFunction;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandRename extends GangCommand {

	public SubCommandRename(ModuleBase owner)
	{
		super("rename", owner);

		this.addArgument(Argument.builder().setPath("gang_name").addResolver(ArgFunction.STRING_FUNCTION).setUsage("gangs.create_specify_name").build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Gang gang = this.getGang();

		String oldName = gang.getName();
		String senderName = sender.getName();

		String gangName = (String) this.getObject("gang_name");

		int length = gangName.length();

		if (length < 3)
		{
			sender.sendLocalizedMessage("gangs.rename_min_length", 3);

			return;
		}

		if (length > 10)
		{
			sender.sendLocalizedMessage("gangs.rename_max_length", 10);
			return;
		}

		GangMember member = gang.getMember(sender.getUniqueId());

		if (member != null && !member.hasPermission(GangRank.OFFICER) && !sender.hasPermission("gangs.admin"))
		{
			sender.sendLocalizedMessage("gangs.must_be_officer");

			for (GangMember leader : gang.getMembersByRank(GangRank.OFFICER))
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

				player.sendEmail("LuckyPrison Gang Attempted Rename", locale.getFormattedMessage("gangs.attempted_rename", oldName, gangName, senderName));
			}

			return;
		}

		for (Player player : gang.getOnlinePlayers())
		{
			player.sendLocalizedMessage("gangs.renamed", gangName, senderName);
		}

		Gangs gangs = Gangs.getInstance();

		for (UUID enemyUUID : gang.getRelations(Relation.ENEMY))
		{
			Gang enemy = gangs.getGang(enemyUUID);

			for (Player enemyPlayer : enemy.getOnlinePlayers())
			{
				enemyPlayer.sendLocalizedMessage("gangs.renamed_enemy", oldName, gangName, senderName);
			}
		}

		for (UUID allyUUID : gang.getRelations(Relation.ALLY))
		{
			Gang ally = gangs.getGang(allyUUID);

			for (Player allyPlayer : ally.getOnlinePlayers())
			{
				allyPlayer.sendLocalizedMessage("gangs.renamed_ally", oldName, gangName, senderName);
			}
		}

		gang.setName(gangName);

		sender.sendLocalizedMessage("gangs.renamed_success", oldName, gangName, gang.getMemberParticipants().size());
	}

}