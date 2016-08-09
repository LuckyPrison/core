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
import com.ulfric.lib.craft.event.player.PlayerPostVanishEvent;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;
import com.ulfric.lib.craft.string.ChatColor;

public class ModuleVanishInterface extends Module {

	public ModuleVanishInterface()
	{
		super("vanish-interface", "Module which owns the /vanish command", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandVanish());

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Scoreboard scoreboard = event.getPlayer().scoreboard();

				scoreboard.addElement(new ElementVanish(scoreboard));
			}

			@Handler
			public void onVanish(PlayerPostVanishEvent event)
			{
				Player player = event.getPlayer();

				Scoreboard scoreboard = player.scoreboard();

				scoreboard.setBelowName(ChatColor.of("GRAY").toString() + "VANISHED");

				ScoreboardElement element = scoreboard.elementFromClazz(ElementVanish.class);

				if (element == null) return;

				element.update();
			}
		});
	}

	private class ElementVanish extends ScoreboardElement
	{
		ElementVanish(Scoreboard board)
		{
			super(board, "vanish");
		}

		@Override
		public String getText()
		{
			if (this.getPlayer().isNotVanished()) return null;

			return "core.scoreboard_vanish";
		}
	}

	private class CommandVanish extends Command
	{
		public CommandVanish()
		{
			super("vanish", ModuleVanishInterface.this, "van", "v");

			this.addPermission("vanish.use");
			this.addOptionalArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setPermission("vanish.others").build());
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
					sender.sendLocalizedMessage("vanish.specify_player");

					return;
				}

				player = (Player) sender;
			}

			if (player.isVanished())
			{
				player.setVanished(false);

				if (player == sender)
				{
					sender.sendLocalizedMessage("vanish.unvanished");

					return;
				}

				sender.sendLocalizedMessage("vanish.unvanished_other", player.getName());

				return;
			}

			player.setVanished(true);

			if (player == sender)
			{
				sender.sendLocalizedMessage("vanish.vanished");

				return;
			}

			sender.sendLocalizedMessage("vanish.vanished_other", player.getName());
		}
	}

}