package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.GameMode;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemStack.EnchantList;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;
import com.ulfric.lib.craft.world.World;
import com.ulfric.lib.craft.world.WorldUtils;

final class ModuleFortunate extends Module {

	public ModuleFortunate()
	{
		super("fortunate", "Makes Fortune awesome", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		World defaultWorld = WorldUtils.getWorlds().get(0);
		Enchantment fortune = Enchantment.byName("fortune");
		GameMode survival = GameMode.of("SURVIVAL");
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onBreak(BlockBreakEvent event)
			{
				Player player = event.getPlayer();

				if (!player.getWorld().equals(defaultWorld)) return;

				if (!survival.equals(player.getGameMode())) return;

				ItemStack hand = event.getHand();

				if (hand == null) return;

				EnchantList enchants = hand.enchants();

				int level = enchants.getLevel(fortune);

				if (level <= 0)
				{
					event.setDroppedItemAmount(4);

					return;
				}

				ItemStack custom = event.getCustomItem();

				if (custom == null) return;

				custom.setAmount((int) Math.round(Math.max(Math.ceil(level / RandomUtils.randomRange(4.2D, 5.2D)), 4)));
			}
		});
	}

}