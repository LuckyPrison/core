package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;

abstract class GangCommand extends Command {

	public GangCommand(String name, ModuleBase owner)
	{
		super(name, owner);

		this.addArgument(Argument.builder().setPath("gang").addSimpleResolver(Gangs.getInstance()::resolveGang).setDefaultValue(cmd ->
		{
			Gangs gangs = Gangs.getInstance();
			GangMember member = gangs.getMember(cmd.getSender().getUniqueId());
			if (member == null) return null;
			return member.getGang();
		}).setPermission("gangs." + this.getName() + ".others").setUsage("gangs.specify_gang").build());
	}

	public final Gang getGang()
	{
		return (Gang) this.getObject("gang");
	}

}