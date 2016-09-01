package com.ulfric.core.rankup;

import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Track;

public final class Rankup {

	Rankup(Track track, Group old, Group next, CurrencyAmount cost)
	{
		this.track = track;
		this.old = old;
		this.next = next;
		this.cost = cost;
	}

	private final Track track;
	private final Group old;
	private final Group next;
	private final CurrencyAmount cost;

	public Track getTrack()
	{
		return this.track;
	}

	public Group getOld()
	{
		return this.old;
	}

	public Group getNext()
	{
		return this.next;
	}

	public CurrencyAmount getCost()
	{
		return this.cost;
	}

}