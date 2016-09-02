package com.ulfric.core.homes;

import java.util.List;
import java.util.stream.Collectors;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandHomes extends Command {

	private final ModuleHomes base;

	public CommandHomes(ModuleHomes owner)
	{
		super("homes", owner);

		this.base = owner;

		super.addEnforcer(Enforcers.IS_PLAYER, "home.must_be_player");

		super.addOptionalArgument(OfflinePlayer.ARGUMENT);
	}

	@Override
	public void run()
	{
		Player sender = (Player) getSender();

		OfflinePlayer target = (OfflinePlayer) super.getObj("offline-player").orElse(sender);

		List<Home> homes = this.base.getHomes(target);

		sender.sendLocalizedMessage("home.list", homes.size());

		sender.sendMessage(homes.stream().map(Home::getName).collect(Collectors.joining(", ")));
	}
}
