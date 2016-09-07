package com.ulfric.core.ptime;

import com.ulfric.lib.coffee.command.ArgFunction;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandPlayerTime extends Command {

	public CommandPlayerTime(ModulePlayerTime owner)
	{
		super("playertime", owner, "ptime");

		this.addEnforcer(Enforcers.IS_PLAYER, "playertime-is-not-player");

		this.addArgument(Argument.builder().addResolver(TimeResolver.ARGUMENT).setPath("time").build());
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		Long time = (Long) this.getObject("time");

		if (time == null)
		{
			player.sendLocalizedMessage("playertime-bad-input");

			return;
		}

		player.setPlayerTime(time, false);

		player.sendLocalizedMessage("playertime-set-time", time);
	}

	// This definitely has room for improvement, but it should work

	private enum TimeResolver implements ArgFunction {

		ARGUMENT;

		@Override
		public Object apply(CommandSender sender, String arg)
		{
			Long time = NumberUtils.parseLong(arg);

			if (time != null)
			{
				return time;
			}

			switch (arg.toLowerCase())
			{
				case "dawn":
				case "morning":
					return 0L;
				case "day":
				case "midday":
					return 6000L;
				case "dusk":
				case "night":
					return 12000L;
				case "midnight":
					return 18000L;
			}

			return null;
		}

	}

}
