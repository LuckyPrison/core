package com.ulfric.core.reward;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.collection.MapUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.potion.PotionEffect;
import com.ulfric.lib.craft.potion.PotionUtils;

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
		if (objects.length > 0)
		{
			Map<String, Object> map = MapUtils.newHashMap(objects);

			Object rawGet = map.get("times");

			if (rawGet instanceof Integer)
			{
				int get = (int) rawGet;

				for (PotionEffect o : this.effects)
				{
					PotionEffect clonedEffect = PotionUtils.newEffect(o.getType(), o.getDuration() * get, o.getAmplifier(), o.isAmbient(), o.hasParticles());

					clonedEffect.apply(player);
				}

				return;
			}
		}

		for (PotionEffect effect : this.effects)
		{
			effect.apply(player);
		}
	}

}