package com.ulfric.core.combattag;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.concurrent.Task;

public enum Tags {

	INSTANCE;

	private final Map<UUID, CombatTag> tasks = Maps.newHashMap();

	public CombatTag getTag(UUID uuid)
	{
		return this.tasks.get(uuid);
	}

	public CombatTag removeTag(UUID uuid)
	{
		CombatTag task = this.tasks.remove(uuid);

		if (task == null) return null;

		task.getTask().cancel();

		return task;
	}

	public boolean createTag(UUID uuid, UUID tagger, long ticks)
	{
		Task task = Task.of(() -> Tags.this.removeTag(uuid)).setDelay(ticks).submit();

		CombatTag tag = new CombatTag(tagger, task);

		CombatTag oldTag = this.tasks.put(uuid, tag);

		if (oldTag == null) return false;

		oldTag.getTask().cancel();

		return true;
	}

	public void clear()
	{
		this.tasks.values().stream().map(CombatTag::getTask).forEach(Task::cancel);
	}

}