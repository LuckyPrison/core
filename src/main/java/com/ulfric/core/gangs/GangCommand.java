package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;

abstract class GangCommand extends Command {

	public GangCommand(String name, GangRank gangPermission, ModuleBase owner)
	{
		super(name, owner);

		this.gangPermission = gangPermission;

		this.addArgument(Argument.builder().setPath("gang").addSimpleResolver(Gangs.getInstance()::resolveGang).setDefaultValue(cmd ->
		{
			Gangs gangs = Gangs.getInstance();
			GangMember member = gangs.getMember(cmd.getSender().getUniqueId());
			if (member == null) return null;
			return member.getGang();
		}).setPermission("gangs." + this.getName() + ".others").setUsage("gangs.specify_gang").build());

		if (gangPermission != null)
		{
			this.addEnforcer(sender ->
			{
				if (sender.hasPermission("gangs.admin")) return true;

				GangMember member = Gangs.getInstance().getMember(sender.getUniqueId());

				if (member == null) return false;

				return member.hasPermission(gangPermission);
			}, "gangs.must_be_" + gangPermission.name().toLowerCase());
		}
	}

	private final GangRank gangPermission;

	public final GangRank getGangPermission()
	{
		return this.gangPermission;
	}

	public final Gang getGang()
	{
		return (Gang) this.getObject("gang");
	}

}