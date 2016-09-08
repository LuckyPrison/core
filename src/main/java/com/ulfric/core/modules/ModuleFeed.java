package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public final class ModuleFeed extends Module {

	public ModuleFeed()
	{
		super("feed", "feed command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandFeed());
	}

	private final class CommandFeed extends Command {

		CommandFeed()
		{
			super("feed", ModuleFeed.this, "eat");

			this.addPermission("feed.use");

			this.addEnforcer(Enforcers.IS_PLAYER, "feed-is-not-player");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();

			player.setFoodLevel(20);
		}

	}

}