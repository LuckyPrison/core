package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public final class ModuleWorkbench extends Module {

	public ModuleWorkbench()
	{
		super("workbench", "Workbench command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandWorkbench());
	}

	private final class CommandWorkbench extends Command {

		CommandWorkbench()
		{
			super("workbench", ModuleWorkbench.this, "wb", "craft", "crafting", "craftingtable", "table");

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


}
