package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

public class ModuleGodmodeInterface extends Module {

	public ModuleGodmodeInterface()
	{
		super("godmode-interface", "Module which owns the /godmode command", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandGodmode());

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Scoreboard scoreboard = event.getPlayer().getScoreboard();

				scoreboard.addElement(new ElementGodmode(scoreboard));
			}

			// TODO post godmode change event?
		});
	}

	private class ElementGodmode extends ScoreboardElement
	{
		ElementGodmode(Scoreboard board)
		{
			super(board, null);
		}

		@Override
		public String getText(Player updater)
		{
			if (!updater.isInvulnerable()) return null;

			return "godmode-element";
		}
	}

	private class CommandGodmode extends Command
	{
		public CommandGodmode()
		{
			super("godmode", ModuleGodmodeInterface.this, "god");

			this.addPermission("godmode.use");
			this.addOptionalArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setPermission("godmode.others").build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			Player player = (Player) this.getObject("player");

			if (player == null)
			{
				if (!(sender instanceof Player))
				{
					sender.sendLocalizedMessage("godmode-specify-player");

					return;
				}

				player = (Player) sender;
			}

			if (player.isInvulnerable())
			{
				player.setInvulnerable(false);

				if (player == sender)
				{
					sender.sendLocalizedMessage("godmode-ungodded");

					return;
				}

				sender.sendLocalizedMessage("godmode-ungodded-other", player.getName());

				return;
			}

			player.setInvulnerable(true);

			if (player == sender)
			{
				sender.sendLocalizedMessage("godmode-godded");

				return;
			}

			sender.sendLocalizedMessage("godmode-godded-other", player.getName());
		}
	}

}