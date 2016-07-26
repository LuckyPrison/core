package com.ulfric.core.modules;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerFirstJoinEvent;

public class ModuleWelcome extends Module {

	public ModuleWelcome()
	{
		super("welcome", "A module to help welcome new players to the server", "1.0.0", "Packet");
	}

	private final List<String> messages = Lists.newArrayList();

	@Override
	public void onModuleEnable()
	{
		for (String string : this.getModuleConfig().getRoot().getStringList("messages", ImmutableList.of()))
		{
			string = string.trim();

			if (string.isEmpty()) continue;

			this.messages.add(string);
		}

		if (!this.messages.isEmpty()) return;

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
		Set<UUID> uuids = Sets.newHashSet();
		Mutable<String> name = new MutableObject<>();

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerFirstJoinEvent event)
			{
				name.setValue(event.getPlayer().getName());
				uuids.clear();
			}
		});

		this.addCommand("welcome", cmd ->
		{
			CommandSender sender = cmd.getSender();

			if (!(sender instanceof Player))
			{
				sender.sendLocalizedMessage("system.must_be_player");

				return;
			}

			String player = name.getValue();

			if (player == null)
			{
				sender.sendLocalizedMessage("core.welcome_err_none");

				return;
			}

			Player from = (Player) sender;

			if (!uuids.add(from.getUniqueId()))
			{
				sender.sendLocalizedMessage("core.welcome_err_already");

				return;
			}

			String message = RandomUtils.randomValue(ModuleWelcome.this.messages);

			if (message == null)
			{
				sender.sendLocalizedMessage("core.welcome_err_format_not_found");

				return;
			}

			// TODO reward the sender

			from.chat(Strings.format(message, player));
		});
	}

}