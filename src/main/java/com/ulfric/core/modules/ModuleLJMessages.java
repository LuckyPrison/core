package com.ulfric.core.modules;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.event.player.PlayerQuitEvent;

public final class ModuleLJMessages extends Module {

	public ModuleLJMessages()
	{
		super("lj-messages", "Leave-Join messages", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				event.setMessage(null);
			}

			@Handler
			public void onQuit(PlayerQuitEvent event)
			{
				event.setMessage(null);
			}
		});
	}

}