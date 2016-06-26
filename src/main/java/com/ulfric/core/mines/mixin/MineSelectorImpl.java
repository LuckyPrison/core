package com.ulfric.core.mines.mixin;

import com.ulfric.lib.coffee.location.Vector;

public class MineSelectorImpl implements IMineSelector {

	private Vector a, b;
	private boolean selecting;

	@Override
	public Vector cornerA()
	{
		return this.a;
	}

	@Override
	public Vector cornerB()
	{
		return this.b;
	}

	@Override
	public void setCornerA(Vector a)
	{
		this.a = a;
	}

	@Override
	public void setCornerB(Vector b)
	{
		this.b = b;
	}

	@Override
	public boolean isSelecting()
	{
		return this.selecting;
	}

	@Override
	public void toggleSelecting()
	{
		this.selecting = !this.selecting;
	}
}
