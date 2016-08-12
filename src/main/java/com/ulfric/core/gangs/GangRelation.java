package com.ulfric.core.gangs;

import java.time.Instant;

public final class GangRelation {

	GangRelation(Relation relation, Instant since)
	{
		this.relation = relation;
		this.since = since;
	}

	private final Relation relation;
	private final Instant since;

	public Relation getRelation()
	{
		return this.relation;
	}

	public Instant getSince()
	{
		return this.since;
	}

}