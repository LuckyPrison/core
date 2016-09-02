package com.ulfric.core.rankup;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandTracks extends Command {

	CommandTracks(ModuleBase owner)
	{
		super("tracks", owner, "ranks", "packs");

		this.addEnforcer(Enforcers.IS_PLAYER, "tracks-must-be-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		Rankups.INSTANCE.openPanel(player);
	}

}