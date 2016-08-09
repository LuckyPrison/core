package com.ulfric.core.control;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;

public class CommandNote extends Command {

	public CommandNote(ModuleBase owner)
	{
		super("note", owner);

		Command add = new CommandAdd(owner);
		this.addCommand(add, CommandKey.builder().add(add.getName()).add("new").add("create").build());

		Command list = new CommandList(owner);
		this.addCommand(list, CommandKey.builder().add(list.getName()).add("all").add("show").add("display").add("fetch").build());
	}

	@Override
	public void run()
	{
		this.getSender().sendLocalizedMessage("notes.usage");
	}

	private class CommandAdd extends Command
	{
		public CommandAdd(ModuleBase owner)
		{
			super("add", owner);

			this.addArgument(NoteHolder.ARGUMENT);
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			String text = this.buildUnusedArgs();

			if (StringUtils.isBlank(text))
			{
				sender.sendLocalizedMessage("notes.specify_text");

				return;
			}

			NoteHolder holder = (NoteHolder) this.getObject("holder");

			Note note = holder.addNote(Notes.getInstance().getHolder(Punisher.valueOf(sender), NoteType.PUNISHMENT_HOLDER), text);

			sender.sendLocalizedMessage("notes.added", holder.getName(), note.getID(), text);
		}
	}

	private class CommandList extends Command
	{
		public CommandList(ModuleBase owner)
		{
			super("list", owner);

			this.addArgument(NoteHolder.ARGUMENT);
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			NoteHolder holder = (NoteHolder) this.getObject("holder");

			Collection<Note> notes = holder.getAllNotes();

			if (notes == null || notes.isEmpty())
			{
				sender.sendLocalizedMessage("notes.none");

				return;
			}

			int size = notes.size();

			Locale locale = sender.getLocale();

			if (size == 1)
			{
				sender.sendMessage(locale.getRawMessage("notes.single"));
			}
			else
			{
				sender.sendMessage(locale.getRawMessage("notes.multiple"), size);
			}

			String raw = locale.getRawMessage("notes.entry");

			for (Note note : notes)
			{
				sender.sendMessage(raw, note.getID(), note.getAuthor().getName(), note.getText());
			}
		}
	}

}