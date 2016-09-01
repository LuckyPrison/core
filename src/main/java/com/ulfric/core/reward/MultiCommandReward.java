package com.ulfric.core.reward;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.coffee.command.CommandUtils;
import com.ulfric.lib.craft.entity.player.Player;

final class MultiCommandReward implements Reward {

	static MultiCommandReward valueOf(List<String> commands)
	{
		Validate.notEmpty(commands);

		commands.forEach(Validate::notBlank);

		return new MultiCommandReward(ImmutableList.copyOf(commands));
	}

	private MultiCommandReward(List<String> commands)
	{
		this.commands = commands;
	}

	private final List<String> commands;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		String name = player.getName();

		for (String command : this.commands)
		{
			CommandUtils.console().runCommand(command.replace("{player}", name));
		}
	}

}