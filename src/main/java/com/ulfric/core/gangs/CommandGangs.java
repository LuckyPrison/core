package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.BaseCommand;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;

public final class CommandGangs extends BaseCommand {

	public CommandGangs(ModuleBase owner)
	{
		super("gangs", owner, "gang", "g", "party", "clan");

		Command command = new SubCommandCreate(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("new").add("n").add("open").build());

		command = new SubCommandDisband(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("delete").add("open").build());

		command = new SubCommandRename(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("setname").add("changename").build());

		command = new SubCommandInfo(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("i").add("inspect").add("check").add("lookup").build());

		command = new SubCommandInvite(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandUninvite(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("deinvite").build());

		command = new SubCommandInvites(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("listinvites").build());

		command = new SubCommandSethome(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandUnsethome(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("deletehome").add("delhome").build());

		command = new SubCommandJoin(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandLeave(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("quit").add("exit").build());

		command = new SubCommandKick(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandSetrank(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandPromote(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandDemote(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandAlly(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandNeutral(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandEnemy(owner);
		this.addCommand(command, CommandKey.singular(command.getName()));

		command = new SubCommandChat(owner);
		this.addCommand(command, CommandKey.builder().add(command.getName()).add("c").build());

		this.addOptionalArgument(Argument.builder().setPath("page").addResolver(Resolvers.INTEGER).setDefaultValue(0).build());
	}

}