package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public final class ModuleEnderChest extends Module {

	public ModuleEnderChest()
	{
		super("ender-chest", "Enderchest command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandEnderChest());
	}

	private final class CommandEnderChest extends Command {

		CommandEnderChest()
		{
			super("enderchest", ModuleEnderChest.this, "echest");

			this.addPermission("enderchest.use");

			this.addEnforcer(Enforcers.IS_PLAYER, "enderchest-is-not-player");
		}

		@Override
		public void run()
		{
			Player sender = (Player) this.getSender();

			// Pretty sure this is all you have to do, no listening required
			sender.openInventory(sender.getEnderChest());
		}

	}

}
