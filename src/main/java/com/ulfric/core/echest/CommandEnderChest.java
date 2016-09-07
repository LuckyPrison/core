package com.ulfric.core.echest;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandEnderChest extends Command {

	public CommandEnderChest(ModuleBase owner)
	{
		super("enderchest", owner, "echest");

		this.addEnforcer(Enforcers.IS_PLAYER, "enderchest-is-not-player");
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();

		if (!sender.hasPermission("enderchest.use"))
		{
			sender.sendLocalizedMessage("enderchest-no-permission");

			return;
		}

		// Pretty sure this is all you have to do, no listening required
		sender.openInventory(sender.getEnderChest());
	}

}
