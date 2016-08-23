package com.ulfric.core.reward;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.collection.MapUtils;
import com.ulfric.lib.coffee.script.Script;
import com.ulfric.lib.craft.entity.player.Player;

final class ScriptReward implements Reward {

	public static ScriptReward valueOf(Script script)
	{
		Validate.notNull(script);

		return new ScriptReward(script);
	}

	private ScriptReward(Script script)
	{
		this.script = script;
	}

	private final Script script;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		this.script.putVariable("player", player);
		this.script.putVariable("reason", reason);

		int length = objects.length;

		if (length > 1)
		{
			Map<String, Object> map = MapUtils.newHashMap(objects);

			map.forEach(this.script::putVariable);
		}

		this.script.run();
	}

}