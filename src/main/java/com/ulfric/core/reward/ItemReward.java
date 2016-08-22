package com.ulfric.core.reward;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.world.World;

public final class ItemReward implements Reward {

	public static ItemReward valueOf(ItemStack stack)
	{
		Validate.isTrue(!ItemUtils.isEmpty(stack));

		return new ItemReward(stack);
	}

	private ItemReward(ItemStack stack)
	{
		this.stack = stack;
	}

	private final ItemStack stack;

	@Override
	public void give(Player player, String reason)
	{
		List<ItemStack> unadded = player.getInventory().addItem(this.stack);

		if (ListUtils.isEmpty(unadded)) return;

		World world = player.getWorld();
		Location location = player.getLocation();

		for (ItemStack item : unadded)
		{
			world.dropItem(location, item);
		}
	}

}