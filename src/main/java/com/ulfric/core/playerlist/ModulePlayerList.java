package com.ulfric.core.playerlist;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.event.player.PlayerQuitEvent;
import com.ulfric.lib.craft.event.server.ServerPingEvent;
import com.ulfric.lib.craft.scoreboard.Scoreboard;

public class ModulePlayerList extends Module {

	public ModulePlayerList()
	{
		super("player-list", "/list, scoreboard element, etc", "1.0.0", "Packet");
	}

	float multiplier;

	@Override
	public void onModuleEnable()
	{
		this.multiplier = this.getModuleConfig().getRoot().getFloat("multiplier", 1.0F);

		for (Player player : PlayerUtils.getOnlinePlayers())
		{
			Scoreboard scoreboard = player.getScoreboard();
			scoreboard.addElement(new ElementPlayercount(scoreboard, this.multiplier));
		}
	}

	@Override
	public void onModuleDisable()
	{
		for (Player player : PlayerUtils.getOnlinePlayers())
		{
			player.getScoreboard().removeElement(ElementPlayercount.class);
		}
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new Command("list", this, "who", "online", "o")
		{
			@Override
			public void run()
			{
				this.getSender().sendLocalizedMessage("list.count", PlayerUtils.countOnlinePlayers() * ModulePlayerList.this.multiplier);
			}
		});

		this.addListener(new Listener(this)
		{
			@Handler
			public void onPing(ServerPingEvent event)
			{
				// TODO setPlayers
			}

			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Scoreboard scoreboard = event.getPlayer().getScoreboard();
				// TODO more elements, and have an element registry somewhere
				scoreboard.addElement(new ElementPlayercount(scoreboard, ModulePlayerList.this.multiplier));

				for (Player allPlayers : PlayerUtils.getOnlinePlayers())
				{
					Scoreboard sb = allPlayers.getScoreboard();
					ElementPlayercount element = sb.elementFromClazz(ElementPlayercount.class);

					if (element == null) continue;

					element.update(allPlayers);
				}
			}

			@Handler
			public void onQuit(PlayerQuitEvent event)
			{
				for (Player allPlayers : PlayerUtils.getOnlinePlayers())
				{
					Scoreboard sb = allPlayers.getScoreboard();
					ElementPlayercount element = sb.elementFromClazz(ElementPlayercount.class);

					if (element == null) continue;

					element.update(allPlayers);
				}
			}
		});
	}

}