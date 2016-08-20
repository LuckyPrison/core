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
import com.ulfric.lib.craft.location.Destination;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.world.WorldUtils;

final class ModuleSpawn extends Module {

	public ModuleSpawn()
	{
		super("spawn", "/spawn & /setspawn", "1.0.0", "Packet");
	}

	Destination destination;

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
				event.getPlayer().teleport(ModuleSpawn.this.destination.getLocation());
			}

			@Handler
			public void onRespawn(PlayerRespawnEvent event)
			{
				event.setLocation(ModuleSpawn.this.destination.getLocation());
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile file = this.getModuleConfig();

		MutableDocument root = file.getRoot();

		Document destDoc = root.getDocument("destination");

		if (destDoc == null)
		{
			this.destination = Destination.newDestination(WorldUtils.getWorlds().get(0).getSpawnPoint(), 5);

			root.set("destination", this.destination.toDocument());

			file.save();

			return;
		}

		this.destination = Destination.fromDocument(destDoc);
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

			ModuleSpawn.this.destination.accept(player);
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

			ModuleSpawn.this.destination = Destination.newDestination(location, 5);

			ConfigFile config = ModuleSpawn.this.getModuleConfig();

			config.getRoot().set("destination", ModuleSpawn.this.destination.toDocument());

			config.save();

			player.sendLocalizedMessage("setspawn.set", location);
		}
	}

}