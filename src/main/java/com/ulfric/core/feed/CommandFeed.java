package com.ulfric.core.feed;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandFeed extends Command {

	public CommandFeed(ModuleFeed base)
	{
		super("feed", base, "eat");

		super.addEnforcer(Enforcers.IS_PLAYER, "feed-is-not-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) super.getSender();

		if (!player.hasPermission("feed.use"))
		{
			player.sendLocalizedMessage("feed-no-permission");

			return;
		}

		player.setFoodLevel(20);
	}

}
