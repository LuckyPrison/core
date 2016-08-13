package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandKick extends GangCommand {

	public SubCommandKick(ModuleBase owner)
	{
		super("kick", owner);

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
			sender.sendLocalizedMessage("gangs.kick_self");

			return;
		}

		Gang gang = this.getGang();

		GangMember member = gang.getMember(targetUUID);

		if (member == null)
		{
			sender.sendLocalizedMessage("gangs.kick_not_member", gang.getName(), target.getName());

			return;
		}

		GangMember senderMember = gang.getMember(sender.getUniqueId());

		if (senderMember != null)
		{
			if (member.hasPermission(senderMember.getRank()))
			{
				sender.sendLocalizedMessage("gangs.cannot_kick_ranked", target.getName(), EnumUtils.format(member.getRank()));

				return;
			}
		}

		gang.removeMember(targetUUID);

		String senderName = sender.getName();
		String targetName = target.getName();

		gang.getOnlinePlayers().forEach(player -> player.sendLocalizedMessage("gangs.kicked_other", senderName, targetName));

		Player player = target.toPlayer();

		Locale locale = player == null ? Locale.getDefault() : player.getLocale();

		target.sendEmail(locale.getRawMessage("gangs.kick_email_subject"), locale.getFormattedMessage("gangs.kick_email_body", gang.getName(), senderName));

		if (player == null) return;

		player.sendLocalizedMessage("gangs.kicked", gang.getName(), senderName);
	}

}