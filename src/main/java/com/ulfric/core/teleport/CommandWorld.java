package com.ulfric.core.teleport;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.string.WordUtils;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.world.World;
import com.ulfric.lib.craft.world.WorldUtils;

final class CommandWorld extends Command {

	public CommandWorld(ModuleBase owner)
	{
		super("world", owner, "changeworld");

		this.addOptionalArgument(Argument.builder().setPath("world").addSimpleResolver(WorldUtils::getWorld).build());

		this.addPermission("teleport.use");

		this.addEnforcer(Enforcers.IS_PLAYER, "world-must-be-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();
		World world = (World) this.getObject("world");

		if (world == null)
		{
			player.sendLocalizedMessage("world-list", WordUtils.merge(WorldUtils.getWorlds()));

			return;
		}

		player.teleport(world.getSpawnPoint());
	}

}