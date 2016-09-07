package com.ulfric.core.fly;

import com.ulfric.core.enchant.ModuleFlightEnchant;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public final class CommandFly extends Command {

	public CommandFly(ModuleFly owner)
	{
		super("fly", owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "fly-is-not-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		boolean flying;

		if (player.containsMetadata("flight"))
		{
			flying = !player.getMetadataAsBoolean("flight");
		}
		else
		{
			flying = true;
		}

		player.setMetadata("flight", flying);

		if (!ModuleFlightEnchant.INSTANCE.enchantPresent(player))
		{
			player.setCanFly(flying);
		}

		player.sendLocalizedMessage("fly-toggle-" + (flying ? "on" : "off"));
	}

}
