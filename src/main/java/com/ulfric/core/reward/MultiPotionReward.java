package com.ulfric.core.reward;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.potion.PotionEffect;

final class MultiPotionReward implements Reward {

	public static MultiPotionReward valueOf(List<PotionEffect> effects)
	{
		Validate.notEmpty(effects);

		for (PotionEffect effect : effects)
		{
			Validate.notNull(effect);
		}

		return new MultiPotionReward(effects.toArray(new PotionEffect[effects.size()]));
	}

	private MultiPotionReward(PotionEffect[] effects)
	{
		this.effects = effects;
	}

	private final PotionEffect[] effects;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		for (PotionEffect effect : this.effects)
		{
			effect.apply(player);
		}
	}

}