package com.ulfric.core.feed;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandFeed extends Command {

	public CommandFeed(ModuleFeed base)
	{
		super("feed", base, "eat");

		this.addEnforcer(Enforcers.IS_PLAYER, "feed-is-not-player");

		this.addPermission("feed.use");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		player.setFoodLevel(20);
	}

}
