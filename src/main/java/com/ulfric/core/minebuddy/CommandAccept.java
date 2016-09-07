package com.ulfric.core.minebuddy;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

final class CommandAccept extends Command {

	CommandAccept(ModuleBase owner)
	{
		super("accept", owner, "yes");

		this.addEnforcer(Enforcers.IS_PLAYER, "minebuddy-must-be-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		if (ModuleMinebuddy.INSTANCE.getBuddy(player.getUniqueId()) != null)
		{
			player.sendLocalizedMessage("minebuddy-already-partnered");

			return;
		}

		Request request = ModuleMinebuddy.INSTANCE.getRequest(player.getUniqueId());

		if (request == null)
		{
			player.sendLocalizedMessage("minebuddy-no-pending-invite");

			return;
		}

		Player partner = PlayerUtils.getOnlinePlayer(request.getSender());

		if (partner == null)
		{
			player.sendLocalizedMessage("minebuddy-offline-invite");

			return;
		}

		if (ModuleMinebuddy.INSTANCE.getBuddy(partner.getUniqueId()) != null)
		{
			player.sendLocalizedMessage("minebuddy-invite-already-partnered");

			return;
		}

		ModuleMinebuddy.INSTANCE.clearInvite(player.getUniqueId());
		ModuleMinebuddy.INSTANCE.clearInvite(partner.getUniqueId());

		Minebuddy buddy = new Minebuddy(player.getUniqueId(), partner.getUniqueId(), request.getSplit() / 100D);

		ModuleMinebuddy.INSTANCE.setBuddy(player.getUniqueId(), buddy);
		ModuleMinebuddy.INSTANCE.setBuddy(partner.getUniqueId(), buddy);

		player.sendLocalizedMessage("minebuddy-created", partner.getName(), request.getSplit());
		partner.sendLocalizedMessage("minebuddy-accepted", player.getName(), request.getSplit());
	}

}