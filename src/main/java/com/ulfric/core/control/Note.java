package com.ulfric.core.control;

import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;

public final class Note implements Comparable<Note> {

	public static Note fromDocument(NoteType type, Document document)
	{
		Notes notes = Notes.getInstance();

		int id = document.getInteger("id");
		NoteHolder holder = notes.getHolder(document.getString("holder"), type);
		NoteHolder author = notes.getHolder(document.getString("author"), type);
		String text = document.getString("text");

		holder.addNote(author, text);

		return new Note(id, type, holder, author, text, false);
	}

	public void into(MutableDocument document)
	{
		document.set("id", this.id);
		document.set("holder", this.holder.getName());
		document.set("author", this.author.getName());
		document.set("text", this.text);
	}

	Note(int id, NoteType type, NoteHolder holder, NoteHolder author, String text, boolean isNew)
	{
		this.id = id;
		this.type = type;
		this.holder = holder;
		this.author = author;
		this.text = text;
		this.isNew = isNew;
	}

	private final int id;
	private final NoteType type;
	private final NoteHolder holder;
	private final NoteHolder author;
	private final String text;
	private final boolean isNew;

	public int getID()
	{
		return this.id;
	}

	public NoteType getType()
	{
		return this.type;
	}

	public NoteHolder getHolder()
	{
		return this.holder;
	}

	public NoteHolder getAuthor()
	{
		return this.author;
	}

	public String getText()
	{
		return this.text;
	}

	public boolean isNew()
	{
		return this.isNew;
	}

	@Override
	public int compareTo(Note note)
	{
		return Integer.compare(this.getID(), note.getID());
	}

}