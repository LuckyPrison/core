package com.ulfric.core.workbench;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandWorkbench extends Command {

	CommandWorkbench(ModuleWorkbench owner)
	{
		super("workbench", owner, "wb", "craft", "crafting", "craftingtable", "table");

		this.addEnforcer(Enforcers.IS_PLAYER, "workbench-is-not-player");

		this.addPermission("workbench.use");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		player.openWorkbench(null, true);
	}

}
