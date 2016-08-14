package com.ulfric.core.gangs;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.function.BooleanResult;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.string.Joiner;

public class CommandGangs extends Command {

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

		this.addOptionalArgument(Argument.builder().setPath("page").addResolver(Resolvers.INTEGER).setDefaultValue(0).build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		List<Command> commands = this.getCommands();

		Validate.notEmpty(commands);

		Iterator<Command> iterator = commands.iterator();

		while (iterator.hasNext())
		{
			Command command = iterator.next();

			BooleanResult result = command.enforce(sender);

			if (result.isSuccess()) continue;

			iterator.remove();
		}

		Validate.notEmpty(commands);

		int page = (int) this.getObject("page");

		int size = commands.size();

		int pages = size / 5;

		page = Math.min(Math.min(0, page), pages);

		size = Math.min(size, pages + 5);

		Joiner joiner = Joiner.lineBreak();

		Locale locale = sender.getLocale();

		for (int x = page; x < size; x++)
		{
			Command command = commands.get(x);
			joiner.append(locale.getFormattedMessage("gangs.help_entry", command.getName(), command.getUsage()));
		}

		joiner.append(locale.getFormattedMessage("gangs.help_page", page, pages));

		sender.sendMessage(joiner.toString());
	}

}