package com.ulfric.core.control;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.collection.MapUtils;

public final class Notes {

	private static final Notes INSTANCE = new Notes();

	public static Notes getInstance()
	{
		return Notes.INSTANCE;
	}

	private Notes() { }

	private final AtomicInteger counter = new AtomicInteger();
	private final Map<String, NoteHolder> holders = Maps.newHashMap();
	private final Map<NoteType, Map<Integer, Note>> notes = MapUtils.enumMapAllOf(NoteType.class, Maps::newHashMap);

	public Note getNote(int id)
	{
		Integer idInteger = id;

		for (Map<Integer, Note> map : this.notes.values())
		{
			Note note = map.get(idInteger);

			if (note == null) continue;

			return note;
		}

		return null;
	}

	public List<Note> getAllNotes(NoteType type)
	{
		return Lists.newArrayList(this.notes.get(type).values());
	}

	public NoteHolder getHolder(Object object, NoteType type)
	{
		String string = String.valueOf(object);

		NoteHolder holder = this.holders.get(string);

		if (holder != null) return holder;

		holder = new NoteHolder(string, type);

		this.holders.put(string, holder);

		return holder;
	}

	public Note newNote(NoteType type, NoteHolder holder, NoteHolder author, String text)
	{
		Note note = new Note(this.counter.getAndIncrement(), type, holder, author, text, true);

		this.registerNote(note);

		return note;
	}

	void registerNote(Note note)
	{
		int id = note.getID();
		Integer idInteger = id;

		this.notes.get(null).put(idInteger, note);
		this.notes.get(note.getType()).put(idInteger, note);

		if (id <= this.counter.get()) return;

		this.counter.set(id);
	}

	void dump()
	{
		this.notes.clear();
		this.holders.clear();
	}

}