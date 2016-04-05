package com.ulfric.core.scoreboard;

import com.ulfric.lib.coffee.event.HandlerMeta;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;

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
			@HandlerMeta
			public void onJoin(PlayerJoinEvent event)
			{
				// TODO more elements, and have an element registry somewhere
				event.getPlayer().scoreboard().addElement(new ElementGodmode());
			}
		});
	}

}