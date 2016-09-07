package com.ulfric.core.minebuddy;

import java.util.UUID;

final class Request {

	Request(UUID sender, int split)
	{
		this.sender = sender;
		this.split = split;
	}

	private final UUID sender;
	private final int split;

	public UUID getSender()
	{
		return this.sender;
	}

	public int getSplit()
	{
		return this.split;
	}

}