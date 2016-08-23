package com.ulfric.core.rankup;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandTracks extends Command {

	public CommandTracks(ModuleBase owner)
	{
		super("tracks", owner, "ranks");

		this.addEnforcer(Enforcers.IS_PLAYER, "tracks.must_be_player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		Rankups.INSTANCE.openPanel(player);
	}

}