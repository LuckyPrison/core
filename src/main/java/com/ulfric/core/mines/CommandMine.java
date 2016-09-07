package com.ulfric.core.mines;

import com.ulfric.core.teleport.ModuleWarps;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandMine extends Command {

	public CommandMine(ModuleBase owner)
	{
		super("mine", owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "mine-must-be-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		ModuleWarps.INSTANCE.getWarp(player.getCurrentGroup().getName().toLowerCase()).accept(player);
	}

}