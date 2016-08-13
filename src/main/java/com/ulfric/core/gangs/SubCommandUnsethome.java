package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;

public class SubCommandUnsethome extends GangCommand {

	public SubCommandUnsethome(ModuleBase owner)
	{
		super("unsethome", GangRank.OFFICER, owner);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Gang gang = this.getGang();

		gang.setHome(null);

		String name = sender.getName();

		gang.getOnlinePlayers().forEach(p -> p.sendLocalizedMessage("gangs.unsethome_by", name));
	}

}