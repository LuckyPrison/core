package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.LivingEntity.Health;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModuleKill extends Module {

	public ModuleKill()
	{
		super("kill", "/kill", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandKill());
	}

	private final class CommandKill extends Command
	{
		CommandKill()
		{
			super("kill", ModuleKill.this, "slay", "murder");

			this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).build());

			this.addPermission("kill.use");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getObject("player");

			Health health = player.health();

			double hp = health.getAsDouble();

			if (hp <= 0)
			{
				this.getSender().sendLocalizedMessage("kill.already_dead", player.getName());

				return;
			}

			health.kill();

			this.getSender().sendLocalizedMessage("kill.killed", player.getName(), hp);
		}
	}

}