package com.ulfric.core.control;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.NamedBase;

public final class NoteHolder extends NamedBase {

	public static final Argument ARGUMENT = Argument.builder().setPath("holder").addSimpleResolver(NoteHolder::valueOf).setUsage("notes.specify_holder").build();

	public static NoteHolder valueOf(String string)
	{
		String parse = string.trim();

		Notes notes = Notes.getInstance();

		if (parse.startsWith("#"))
		{
			Integer integer = NumberUtils.parseInteger(parse.substring(1));

			if (integer != null) return notes.getHolder(parse, NoteType.PUNISHMENT);

			return null;
		}

		if (parse.startsWith("@"))
		{
			if (parse.substring(1).trim().isEmpty()) return null;

			return notes.getHolder(parse, NoteType.THREAD);
		}

		PunishmentHolder holder = PunishmentHolder.valueOf(parse);

		if (holder == null) return null;

		return notes.getHolder(holder, NoteType.PUNISHMENT_HOLDER);
	}

	NoteHolder(String name, NoteType type)
	{
		super(name);
		this.type = type;
	}

	private final NoteType type;
	private Set<Note> notes;

	public NoteType getType()
	{
		return this.type;
	}

	public List<Note> getAllNotes()
	{
		if (this.notes == null) return ImmutableList.of();

		return ImmutableList.copyOf(this.notes);
	}

	public List<Note> getNotesBy(Punisher punisher)
	{
		Validate.notNull(punisher);

		List<Note> allNotes = this.getAllNotes();

		if (allNotes == null) return null;

		Iterator<Note> iterator = allNotes.iterator();

		while (iterator.hasNext())
		{
			Note note = iterator.next();

			if (note.getAuthor().equals(punisher)) continue;

			iterator.remove();
		}

		return allNotes;
	}

	public boolean removeNote(Note note)
	{
		Validate.notNull(note);

		if (this.notes == null) return false;

		Iterator<Note> iter = this.notes.iterator();

		while (iter.hasNext())
		{
			if (iter.next() != note) continue;

			iter.remove();

			return true;
		}

		return false;
	}

	public Note addNote(NoteHolder author, String text)
	{
		Validate.notNull(author);
		Validate.notBlank(text);

		Note note = Notes.getInstance().newNote(this.type, this, author, text);

		return this.addNote(note);
	}

	Note addNote(Note note)
	{
		Validate.notNull(note);

		if (this.notes == null)
		{
			this.notes = Sets.newTreeSet();
		}

		this.notes.add(note);

		return note;
	}

}