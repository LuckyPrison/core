package com.ulfric.core.pweather;

import com.ulfric.lib.coffee.module.Module;

public final class ModulePlayerWeather extends Module {

	public ModulePlayerWeather()
	{
		super("player-weather", "Player weather command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandPlayerWeather(this));
	}

}
