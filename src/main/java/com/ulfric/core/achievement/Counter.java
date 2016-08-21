package com.ulfric.core.achievement;

class Counter {

	public Counter(int value)
	{
		this.value = value;
	}

	public Counter(int value, boolean changed)
	{
		this.value = value;
		this.changed = changed;
	}

	private int value;
	private boolean changed;

	public int toInt()
	{
		return this.value;
	}

	public boolean hasBeenChanged()
	{
		return this.changed;
	}

	public void untouch()
	{
		this.changed = false;
	}

	public void increment()
	{
		this.value++;

		this.changed = true;
	}

	public void increment(int amount)
	{
		if (amount <= 0) return;

		this.value += amount;

		this.changed = true;
	}

}