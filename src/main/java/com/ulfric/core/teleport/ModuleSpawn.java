package com.ulfric.core.teleport;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerFirstJoinEvent;
import com.ulfric.lib.craft.event.player.PlayerRespawnEvent;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.Destination;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.world.WorldUtils;

final class ModuleSpawn extends Module {

	public ModuleSpawn()
	{
		super("spawn", "/spawn & /setspawn", "1.0.0", "Packet");
	}

	Warp warp;

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandSpawn());
		this.addCommand(new CommandSetSpawn());

		this.addListener(new Listener(this)
		{
			@Handler
			public void onFirstJoin(PlayerFirstJoinEvent event)
			{
				event.getPlayer().teleport(ModuleSpawn.this.warp.getLocation());
			}

			@Handler
			public void onRespawn(PlayerRespawnEvent event)
			{
				event.setLocation(ModuleSpawn.this.warp.getLocation());
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile file = this.getModuleConfig();

		MutableDocument root = file.getRoot();

		Document warpDoc = root.getDocument("warp");

		if (warpDoc == null)
		{
			this.warp = Warp.newWarp("spawn", Destination.newDestination(WorldUtils.getWorlds().get(0).getSpawnPoint(), 5), Material.of("GRASS").toItem());

			root.set("warp", this.warp.toDocument());

			file.save();

			return;
		}

		this.warp = Warp.fromDocument("spawn", warpDoc);
	}

	private class CommandSpawn extends Command
	{
		public CommandSpawn()
		{
			super("spawn", ModuleSpawn.this);

			this.addEnforcer(Enforcers.IS_PLAYER, "spawn.must_be_player");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();

			ModuleSpawn.this.warp.accept(player);
		}
	}

	private class CommandSetSpawn extends Command
	{
		public CommandSetSpawn()
		{
			super("setspawn", ModuleSpawn.this);

			this.addPermission("setspawn.use");

			this.addEnforcer(Enforcers.IS_PLAYER, "spawn.must_be_player");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();
			Location location = player.getLocation();

			ModuleSpawn.this.warp = Warp.newWarp("spawn", Destination.newDestination(location, 5), Material.of("GRASS").toItem());

			ConfigFile config = ModuleSpawn.this.getModuleConfig();

			config.getRoot().set("warp", ModuleSpawn.this.warp.toDocument());

			config.save();

			player.sendLocalizedMessage("setspawn.set", location);
		}
	}

}