package com.ulfric.core.reward;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.collection.MapUtils;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.entity.EntityType;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.location.Location;

final class EntityReward implements Reward {

	static EntityReward valueOf(EntityType type)
	{
		Validate.notNull(type);

		return new EntityReward(type);
	}

	private EntityReward(EntityType type)
	{
		this.type = type;
	}

	private final EntityType type;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		Location location = null;
		if (objects.length > 1)
		{
			Map<String, Object> objs = MapUtils.newHashMap(objects);

			Block block = (Block) objs.get("block");

			if (block != null)
			{
				location = block.getLocation();
			}
		}
		if (location == null)
		{
			location = player.getLocation();
		}

		location.getWorld().spawnEntity(location, this.type);
	}

}