package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;

public final class CommandGangs extends BaseCommand {

	public CommandGangs(ModuleBase owner)
	{
		super("gangs", owner, "gang", "g", "party", "clan");

		this.addCommand(new SubCommandCreate(owner));
		this.addCommand(new SubCommandDisband(owner));
		this.addCommand(new SubCommandRename(owner));
		this.addCommand(new SubCommandInfo(owner));
		this.addCommand(new SubCommandInvite(owner));
		this.addCommand(new SubCommandUninvite(owner));
		this.addCommand(new SubCommandInvites(owner));
		this.addCommand(new SubCommandSethome(owner));
		this.addCommand(new SubCommandUnsethome(owner));
		this.addCommand(new SubCommandJoin(owner));
		this.addCommand(new SubCommandLeave(owner));
		this.addCommand(new SubCommandKick(owner));
		this.addCommand(new SubCommandPromote(owner));
		this.addCommand(new SubCommandDemote(owner));
		this.addCommand(new SubCommandAlly(owner));
		this.addCommand(new SubCommandNeutral(owner));
		this.addCommand(new SubCommandEnemy(owner));
		this.addCommand(new SubCommandChat(owner));

		this.addOptionalArgument(Argument.builder().setPath("page").addResolver(Resolvers.INTEGER).setDefaultValue(0).build());
	}

}