package com.ulfric.core.reward;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.potion.PotionEffect;

final class PotionReward implements Reward {

	public static PotionReward valueOf(PotionEffect effect)
	{
		Validate.notNull(effect);

		return new PotionReward(effect);
	}

	private PotionReward(PotionEffect effect)
	{
		this.effect = effect;
	}

	private final PotionEffect effect;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		this.effect.apply(player);
	}

}