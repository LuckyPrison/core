package com.ulfric.core.control;

public interface Noteable {

	default NoteHolder getNoteInterface()
	{
		return Notes.getInstance().getHolder(this, this.getNoteType());
	}

	NoteType getNoteType();

}