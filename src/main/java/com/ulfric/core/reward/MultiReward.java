package com.ulfric.core.reward;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.ulfric.lib.craft.entity.player.Player;

final class MultiReward implements Reward {

	public static MultiReward valueOf(List<Reward> rewards)
	{
		Validate.notEmpty(rewards);
		Validate.noNullElements(rewards);

		return new MultiReward(ImmutableList.copyOf(rewards));
	}

	private MultiReward(List<Reward> rewards)
	{
		this.rewards = rewards;
	}

	private final List<Reward> rewards;

	@Override
	public void give(Player player, String reason)
	{
		for (Reward reward : this.rewards)
		{
			reward.give(player, reason);
		}
	}

}