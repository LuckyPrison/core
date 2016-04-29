package com.ulfric.core.scoreboard;

import java.util.Objects;

import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.event.ListenerMeta;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.event.player.PlayerQuitEvent;
import com.ulfric.lib.craft.scoreboard.Scoreboard;

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
			@ListenerMeta
			public void onJoin(PlayerJoinEvent event)
			{
				Scoreboard scoreboard = event.getPlayer().scoreboard();
				// TODO more elements, and have an element registry somewhere
				scoreboard.addElement(new ElementGodmode(scoreboard));
				scoreboard.addElement(new ElementPlayercount(scoreboard));

				PlayerUtils.streamOnlinePlayers().map(Player::scoreboard).map(sb -> sb.elementFromClazz(ElementPlayercount.class)).filter(Objects::nonNull).forEach(ElementPlayercount::invalidate);
			}

			@ListenerMeta
			public void onQuit(PlayerQuitEvent event)
			{
				PlayerUtils.streamOnlinePlayers().map(Player::scoreboard).map(sb -> sb.elementFromClazz(ElementPlayercount.class)).filter(Objects::nonNull).forEach(ElementPlayercount::invalidate);
			}
		});
	}

}