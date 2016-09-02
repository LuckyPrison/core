package com.ulfric.core.achievement;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandAchievements extends Command {

	public CommandAchievements(ModuleBase owner)
	{
		super("achievements", owner, "achievement", "goals", "goal");

		this.addEnforcer(Enforcers.IS_PLAYER, "achievements-must-be-player");
	}

	@Override
	public void run()
	{
		Categories.INSTANCE.openPanel((Player) this.getSender());
	}

}