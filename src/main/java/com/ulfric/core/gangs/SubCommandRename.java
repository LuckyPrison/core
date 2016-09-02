package com.ulfric.core.gangs;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.time.TimeUtils;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandRename extends GangCommand {

	public SubCommandRename(ModuleBase owner)
	{
		super("rename", GangRank.LIEUTENANT, owner, "setname", "changename");

		this.addArgument(Argument.builder().setPath("gang_name").addResolver(Resolvers.STRING).setUsage("gangs-create-specify-name").build());
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
			sender.sendLocalizedMessage("gangs-name-min-length", 3);

			return;
		}

		if (length > 10)
		{
			sender.sendLocalizedMessage("gangs-name-max-length", 10);
			return;
		}

		if (!StringUtils.isAlpha(gangName))
		{
			sender.sendLocalizedMessage("gangs-name-must-be-alpha");

			return;
		}

		Gangs gangs = Gangs.getInstance();

		Gang alreadyExists = gangs.getGang(gangName);

		if (alreadyExists != null)
		{
			sender.sendLocalizedMessage("gangs-create-name-taken", gang.getName(), TimeUtils.formatMillis(TimeUtils.instantSince(gang.getCreated()).toEpochMilli(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS));

			return;
		}

		for (Player player : gang.getOnlinePlayers())
		{
			new GangStatusEvent(player, gang, gang).fire();
			if (player == sender) continue;
			player.sendLocalizedMessage("gangs-renamed", gangName, senderName);
		}

		for (UUID enemyUUID : gang.getRelations(Relation.ENEMY))
		{
			Gang enemy = gangs.getGang(enemyUUID);

			for (Player enemyPlayer : enemy.getOnlinePlayers())
			{
				enemyPlayer.sendLocalizedMessage("gangs-renamed-enemy", oldName, gangName, senderName);
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

		sender.sendLocalizedMessage("gangs-renamed-success", gangName);
	}

}