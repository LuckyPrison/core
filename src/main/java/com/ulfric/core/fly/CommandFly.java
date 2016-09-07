package com.ulfric.core.fly;

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

		player.setCanFly(!player.canFly());

		if (player.canFly())
		{
			player.sendLocalizedMessage("fly-toggle-on");
		}
		else
		{
			player.sendLocalizedMessage("fly-toggle-off");
		}
	}

}
