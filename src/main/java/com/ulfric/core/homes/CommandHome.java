package com.ulfric.core.homes;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.location.Location;

public class CommandHome extends Command {

	public CommandHome(ModuleBase owner)
	{
		super("home", owner);

		super.addEnforcer(Enforcers.IS_PLAYER, "home.must_be_player");

		super.addOptionalArgument(OfflinePlayer.ARGUMENT);
	}

	@Override
	public void run()
	{
		Player sender = (Player) super.getSender();

		Player target = (Player) super.getObject("offline-player");

		if (target != null)
		{
			if (!sender.hasPermission("home.other"))
			{
				sender.sendLocalizedMessage("home.no_permission");

				return;
			}
		}
		else
		{
			target = sender;
		}

		Location home;

		PlayerUtils.getPlayerData();
	}

}
