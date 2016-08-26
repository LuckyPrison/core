package com.ulfric.core.control;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;

public class CommandNote extends Command {

	public CommandNote(ModuleBase owner)
	{
		super("note", owner, "notes");

		Command add = new CommandAdd(owner);
		this.addCommand(add);

		Command list = new CommandList(owner);
		this.addCommand(list);
	}

	@Override
	public void run()
	{
		this.getSender().sendLocalizedMessage("control.notes_usage");
	}

	private class CommandAdd extends Command
	{
		public CommandAdd(ModuleBase owner)
		{
			super("add", owner, "new", "create");

			this.addArgument(NoteHolder.ARGUMENT);
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			String text = this.buildUnusedArgs();

			if (StringUtils.isBlank(text))
			{
				sender.sendLocalizedMessage("control.notes_specify_text");

				return;
			}

			NoteHolder holder = (NoteHolder) this.getObject("holder");

			Note note = holder.addNote(Notes.getInstance().getHolder(Punisher.valueOf(sender), NoteType.PUNISHMENT_HOLDER), text);

			sender.sendLocalizedMessage("control.notes_added", holder.getName(), note.getID(), text);
		}
	}

	private class CommandList extends Command
	{
		public CommandList(ModuleBase owner)
		{
			super("list", owner, "all", "show", "display", "fetch");

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
				sender.sendLocalizedMessage("control.notes_none");

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