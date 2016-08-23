package com.ulfric.core.reward;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ulfric.lib.coffee.collection.MapUtils;
import com.ulfric.lib.coffee.script.Script;
import com.ulfric.lib.craft.entity.player.Player;

final class MultiScriptReward implements Reward {

	public static MultiScriptReward valueOf(List<Script> scripts)
	{
		Validate.notEmpty(scripts);
		Validate.noNullElements(scripts);

		return new MultiScriptReward(ImmutableList.copyOf(scripts));
	}

	private MultiScriptReward(List<Script> scripts)
	{
		this.scripts = scripts;
	}

	private final List<Script> scripts;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		Map<String, Object> map = objects.length > 1 ? MapUtils.newHashMap(objects) : ImmutableMap.of();

		for (Script script : this.scripts)
		{
			script.putVariable("player", player);
			script.putVariable("reason", reason);

			map.forEach(script::putVariable);

			script.run();
		}
	}

}