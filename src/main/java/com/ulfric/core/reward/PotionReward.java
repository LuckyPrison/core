package com.ulfric.core.reward;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.collection.MapUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.potion.PotionEffect;
import com.ulfric.lib.craft.potion.PotionUtils;

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
		if (objects.length > 0)
		{
			Map<String, Object> map = MapUtils.newHashMap(objects);

			Object rawGet = map.get("times");

			if (rawGet instanceof Integer)
			{
				int get = (int) rawGet;

				PotionEffect o = this.effect;
				PotionEffect clonedEffect = PotionUtils.newEffect(o.getType(), o.getDuration() * get, o.getAmplifier(), o.isAmbient(), o.hasParticles());

				clonedEffect.apply(player);

				return;
			}
		}

		this.effect.apply(player);
	}

}