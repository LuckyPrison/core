package com.ulfric.core.reward;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.world.World;

final class MultiItemReward implements Reward {

	static MultiItemReward valueOf(List<ItemStack> stacks)
	{
		Validate.notEmpty(stacks);

		for (ItemStack stack : stacks)
		{
			Validate.isTrue(!ItemUtils.isEmpty(stack));
		}

		return new MultiItemReward(stacks.toArray(new ItemStack[stacks.size()]));
	}

	private MultiItemReward(ItemStack[] stacks)
	{
		this.stacks = stacks;
	}

	private final ItemStack[] stacks;

	@Override
	public void give(Player player, String reason, Object... objects)
	{
		List<ItemStack> unadded = player.getInventory().addItems(this.stacks);

		if (ListUtils.isEmpty(unadded)) return;

		World world = player.getWorld();
		Location location = player.getLocation();

		for (ItemStack item : unadded)
		{
			world.dropItem(location, item);
		}
	}

}