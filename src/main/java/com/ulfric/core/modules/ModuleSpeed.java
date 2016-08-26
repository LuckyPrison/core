package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public final class ModuleSpeed extends Module {

	public ModuleSpeed()
	{
		super("speed", "Command for controling speed", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandSpeed(this));
	}

	private static final class CommandSpeed extends Command
	{
		public CommandSpeed(ModuleBase owner)
		{
			super("speed", owner);

			this.addEnforcer(Enforcers.IS_PLAYER, "speed.must_be_player");

			this.addArgument(Argument.builder().setPath("value").addResolver(Resolvers.FLOAT).setUsage("speed.specify_speed").build());
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();

			float speed = (float) this.getObject("value");

			speed = speed / 10;

			if (player.isFlying())
			{
				player.setFlyingSpeed(speed);

				return;
			}

			player.setWalkingSpeed(speed);
		}
	}

}