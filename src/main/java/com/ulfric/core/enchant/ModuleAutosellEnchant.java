package com.ulfric.core.enchant;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import com.ulfric.core.economy.ModuleSell;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.event.player.PlayerItemHeldEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.enchant.Enchant;

final class ModuleAutosellEnchant extends Module {

	ModuleAutosellEnchant()
	{
		super("autosell-enchant", "Autosell enchantment", "1.0.0", "Packet");
	}

	Set<Player> set = SetUtils.newWeakHashSet();

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler
			public void onBreak(BlockBreakEvent event)
			{
				event.getPlayer().getMainHand().enchants().add(Enchant.of(EnchantmentAutoSell.INSTANCE, 1));
			}
			@Handler
			public void onItem(PlayerItemHeldEvent event)
			{
				ItemStack newItem = event.getNewItem();

				if (newItem == null) return;

				if (newItem.enchants().getLevel(EnchantmentAutoSell.INSTANCE) > 0)
				{
					ModuleAutosellEnchant.this.set.add(event.getPlayer());

					return;
				}

				ItemStack oldItem = event.getOldItem();

				if (oldItem == null) return;

				if (oldItem.enchants().getLevel(EnchantmentAutoSell.INSTANCE) == 0)

				ModuleAutosellEnchant.this.set.remove(event.getPlayer());
			}
		});

		MutableInt count = new MutableInt(1);

		ThreadUtils.runRepeating(() ->
		{
			int current = count.intValue();

			Iterator<Player> iterator = this.set.iterator();

			while (iterator.hasNext())
			{
				Player next = iterator.next();

				ItemStack hand = next.getMainHand();

				if (hand == null)
				{
					iterator.remove();

					continue;
				}

				int level = hand.enchants().getLevel(EnchantmentAutoSell.INSTANCE);

				if (level == 0)
				{
					iterator.remove();

					continue;
				}

				if (current > level) continue;

				Group group = next.getCurrentGroup();

				if (group == null)
				{
					iterator.remove();

					continue;
				}

				ModuleSell.INSTANCE.sellAll(next, group.getName());
			}

			count.increment();

			if (count.intValue() > 3)
			{
				count.setValue(1);
			}
		}, 200);
	}

	@Override
	public void onModuleEnable()
	{
		EnchantmentAutoSell.INSTANCE.register();
	}

	@Override
	public void onModuleDisable()
	{
		EnchantmentAutoSell.INSTANCE.unregister();
	}

}