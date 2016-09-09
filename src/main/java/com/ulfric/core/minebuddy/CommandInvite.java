package com.ulfric.core.minebuddy;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

final class CommandInvite extends Command {

	CommandInvite(ModuleBase owner)
	{
		super("invite", owner, "ask");

		this.addEnforcer(Enforcers.IS_PLAYER, "minebuddy-must-be-player");

		this.addArgument(Argument.builder().setPath("split").addSimpleResolver(str ->
		{
			Integer iv = NumberUtils.parseInteger(str);

			if (iv == null) return null;

			return Math.max(Math.min(iv, 0), 100);
		}).setDefaultValue(50).build());
		this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setUsage("minebuddy-specify-player").build());
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();
		Player target = (Player) this.getObject("player");

		if (player.getUniqueId().equals(target.getUniqueId()))
		{
			player.sendLocalizedMessage("minebuddy-invite-self");

			return;
		}

		int split = (int) this.getObject("split");

		ModuleMinebuddy.INSTANCE.setInvite(target.getUniqueId(), new Request(player.getUniqueId(), split));

		target.sendLocalizedMessage("minebuddy-invited", player.getName(), split);

		player.sendLocalizedMessage("minebuddy-invite-sent", target.getName(), split);
	}

}