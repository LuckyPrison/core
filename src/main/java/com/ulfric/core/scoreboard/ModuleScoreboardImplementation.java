package com.ulfric.core.scoreboard;

import com.ulfric.lib.coffee.economy.BalanceChangeEvent;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.event.player.PlayerQuitEvent;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

public class ModuleScoreboardImplementation extends Module {

	public ModuleScoreboardImplementation()
	{
		super("scoreboard-implementation", "Scoreboard implementation module", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		// TODO player configurability once Nate writes up his Data2 guide
		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Scoreboard scoreboard = event.getPlayer().getScoreboard();
				// TODO more elements, and have an element registry somewhere
				scoreboard.addElement(new ElementBalance(scoreboard));
				scoreboard.addElement(new ElementPlayercount(scoreboard));

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

			@Handler
			public void onBalance(BalanceChangeEvent event)
			{
				if (!event.getCurrency().equals(Currency.getDefaultCurrency())) return;

				Player player = PlayerUtils.getPlayer(event.getAccount().getUniqueId());

				if (player == null) return;

				ScoreboardElement element = player.getScoreboard().elementFromClazz(ElementBalance.class);

				if (element == null) return;

				element.update(player);
			}
		});
	}

}