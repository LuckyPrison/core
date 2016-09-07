package com.ulfric.core.minebuddy;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandLeave extends Command {

	public CommandLeave(ModuleBase owner)
	{
		super("leave", owner, "quit", "exit");

		this.addEnforcer(Enforcers.IS_PLAYER, "minebuddy-must-be-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		Minebuddy mb = ModuleMinebuddy.INSTANCE.getBuddy(player.getUniqueId());

		if (mb == null)
		{
			player.sendLocalizedMessage("minebuddy-no-partner");

			return;
		}

		ModuleMinebuddy.INSTANCE.clear(mb);

		// TODO messages
	}

}