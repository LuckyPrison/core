package com.ulfric.core.gangs;

import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandUninvite extends InviteGangCommand {

	public SubCommandUninvite(ModuleBase owner)
	{
		super("uninvite", owner, "deinvite");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		OfflinePlayer offlinePlayer = (OfflinePlayer) this.getObject("offline-player");
		Player onlinePlayer = offlinePlayer.toPlayer();
		UUID targetUUID = offlinePlayer.getUniqueId();

		Gang gang = this.getGang();

		GangMember member = gang.getMember(targetUUID);

		if (member != null)
		{
			sender.sendLocalizedMessage("gangs-uninvite-already-member", member.getName(), EnumUtils.format(member.getRank()));

			return;
		}

		if (!gang.isInvited(targetUUID))
		{
			sender.sendLocalizedMessage("gangs-uninvite-not-invited", offlinePlayer.getName());

			return;
		}

		gang.removeInvite(targetUUID);

		sender.sendLocalizedMessage("gangs-uninvited-success", offlinePlayer.getName());

		if (onlinePlayer == null) return;

		onlinePlayer.sendLocalizedMessage("gangs-uninvited-to", sender.getName(), gang.getName());
	}

}