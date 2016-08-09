package com.ulfric.core.control;

import org.apache.commons.lang3.Validate;

import com.ulfric.config.Document;

public enum NoteType {

	PUNISHMENT_HOLDER,
	PUNISHMENT,
	THREAD;

	public Note fromDocument(Document document)
	{
		Validate.notNull(document);

		return Note.fromDocument(this, document);
	}

}