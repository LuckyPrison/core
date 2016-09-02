package com.ulfric.core.modules;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerFirstJoinEvent;

public class ModuleWelcome extends Module {

	public ModuleWelcome()
	{
		super("welcome", "A module to help welcome new players to the server", "1.0.0", "Packet");
	}

	List<String> messages;
	String currentName;
	Set<UUID> uuids = Sets.newHashSet();

	@Override
	public void onModuleEnable()
	{
		boolean empty = true;

		for (String string : this.getModuleConfig().getRoot().getStringList("messages", ImmutableList.of()))
		{
			string = string.trim();

			if (string.isEmpty()) continue;

			empty = false;

			this.messages.add(string);
		}

		if (!empty) return;

		this.messages.add("Welcome, {0}!");
	}

	@Override
	public void onModuleDisable()
	{
		this.messages.clear();
	}

	@Override
	public void onFirstEnable()
	{
		this.messages = Lists.newArrayList();

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerFirstJoinEvent event)
			{
				ModuleWelcome.this.currentName = event.getPlayer().getName();
				ModuleWelcome.this.uuids.clear();
			}
		});

		this.addCommand(new CommandWelcome());
	}

	private final class CommandWelcome extends Command
	{
		public CommandWelcome()
		{
			super("welcome", ModuleWelcome.this);

			this.addEnforcer(Enforcers.IS_PLAYER, "welcome-must-be-player");
		}

		@Override
		public void run()
		{
			Player sender = (Player) this.getSender();

			String name = ModuleWelcome.this.currentName;

			if (name == null)
			{
				sender.sendLocalizedMessage("welcome-no-recent-player");

				return;
			}

			if (!ModuleWelcome.this.uuids.add(sender.getUniqueId()))
			{
				sender.sendLocalizedMessage("welcome-already-welcomed", name);

				return;
			}

			String message = RandomUtils.randomValue(ModuleWelcome.this.messages);

			if (message == null)
			{
				sender.sendLocalizedMessage("welcome-no-available-messages");

				return;
			}

			// TODO reward the sender

			sender.chat(Strings.format(message, name));
		}
	}

}