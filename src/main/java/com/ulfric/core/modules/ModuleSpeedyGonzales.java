package com.ulfric.core.modules;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;

public class ModuleSpeedyGonzales extends Module {

	public ModuleSpeedyGonzales()
	{
		super("speedy-gonzales", "Module which makes you go faster", "1.0.0", "Packet");
	}

	float speed;

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				event.getPlayer().setWalkingSpeed(ModuleSpeedyGonzales.this.speed);
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		this.speed = this.getModuleConfig().getRoot().getFloat("speed", 0.25F);
	}

}