package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

public class SubCommandSethome extends GangCommand {

	public SubCommandSethome(ModuleBase owner)
	{
		super("sethome", GangRank.OFFICER, owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "gangs.sethome_must_be_player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();
		Gang gang = this.getGang();

		gang.setHome(player.getLocation());

		String playerName = player.getName();
		gang.getOnlinePlayers().forEach(p -> p.sendLocalizedMessage("gangs.sethome_by", playerName));
	}

}