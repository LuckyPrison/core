package com.ulfric.core.combattag;

import java.util.UUID;

import com.ulfric.lib.coffee.concurrent.Task;

public final class CombatTag {

	CombatTag(UUID tagger, Task task)
	{
		this.tagger = tagger;
		this.task = task;
		this.created = System.currentTimeMillis();
	}

	private final UUID tagger;
	private final Task task;
	private final long created;

	public UUID getTagger()
	{
		return this.tagger;
	}

	public Task getTask()
	{
		return this.task;
	}

	public long getCreated()
	{
		return this.created;
	}

}