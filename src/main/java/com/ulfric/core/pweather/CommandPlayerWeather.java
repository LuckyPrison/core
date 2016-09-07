package com.ulfric.core.pweather;

import com.ulfric.lib.coffee.command.ArgFunction;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.world.WeatherType;

final class CommandPlayerWeather extends Command {

	public CommandPlayerWeather(ModulePlayerWeather owner)
	{
		super("playerweather", owner, "pweather");

		this.addEnforcer(Enforcers.IS_PLAYER, "playerweather-is-not-player");

		this.addArgument(Argument.builder().addResolver(WeatherResolver.ARGUMENT).setPath("weather").build());
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		WeatherType weather = (WeatherType) this.getObject("weather");

		if (weather == null)
		{
			player.sendLocalizedMessage("playerweather-invalid-weather");

			return;
		}

		player.setPlayerWeather(weather);

		player.sendLocalizedMessage("playerweather-set", weather.getName());
	}

	// This isn't very well localized, can't think of anything other than keywords

	private enum WeatherResolver implements ArgFunction {

		ARGUMENT;

		@Override
		public Object apply(CommandSender sender, String arg)
		{
			switch (arg.toLowerCase())
			{
				case "none":
				case "clear":
				case "sun":
				case "sunny":
				case "shine":
					return WeatherType.of("CLEAR");
				case "rain":
				case "raining":
				case "wet":
				case "overcast":
				case "downfall":
					return WeatherType.of("DOWNFALL");
			}
			return null;
		}

	}

}
