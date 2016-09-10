package com.ulfric.core.crate;

import com.ulfric.core.reward.Reward;
import com.ulfric.lib.craft.inventory.item.ItemStack;

final class IconnedReward {

	private final Reward reward;
	private final ItemStack item;

	IconnedReward(Reward reward, ItemStack item)
	{
		this.reward = reward;
		this.item = item;
	}

	public Reward getReward()
	{
		return this.reward;
	}

	public ItemStack getItem()
	{
		return this.item.copy();
	}

}
