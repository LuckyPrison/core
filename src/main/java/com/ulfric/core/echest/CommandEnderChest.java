package com.ulfric.core.echest;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public final class CommandEnderchest extends Command {

	public CommandEnderchest(ModuleEnderchest owner)
	{
		super("enderchest", owner, "echest");

		this.addEnforcer(Enforcers.IS_PLAYER, "enderchest-is-not-player");

		this.addPermission("enderchest.use");
	}

	@Override
	public void run()
	{
		Player sender = (Player) this.getSender();

		// Pretty sure this is all you have to do, no listening required
		sender.openInventory(sender.getEnderChest());
	}

}
