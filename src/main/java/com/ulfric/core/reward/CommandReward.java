package com.ulfric.core.reward;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.command.CommandUtils;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandReward implements Reward {

	static CommandReward valueOf(String command)
	{
		Validate.notBlank(command);

		return new CommandReward(command);
	}

	private CommandReward(String command)
	{
		this.command = command;
	}

	private final String command;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		CommandUtils.console().runCommand(this.command.replace("{player}", player.getName()));
	}

}