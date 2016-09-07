package com.ulfric.core.minebuddy;

import java.util.UUID;

public final class Minebuddy {

	Minebuddy(UUID player1, UUID player2, double split)
	{
		this.player1 = player1;
		this.player2 = player2;
		this.split = split;
	}

	private final UUID player1;
	private final UUID player2;
	private final double split;

	public UUID getPlayer1()
	{
		return this.player1;
	}

	public UUID getPlayer2()
	{
		return this.player2;
	}

	public UUID getOther(UUID uuid)
	{
		if (uuid.equals(this.player1)) return this.player2;

		return this.player1;
	}

	public double getSplit()
	{
		return this.split;
	}

}