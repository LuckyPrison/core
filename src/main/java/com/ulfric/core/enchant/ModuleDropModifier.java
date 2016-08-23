package com.ulfric.core.enchant;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.collection.EnumishMap;
import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.object.ObjectUtils;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;

final class ModuleDropModifier extends Module {

	public ModuleDropModifier()
	{
		super("drop-modifier", "ItemStack drop modifier", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addModule(new ModuleAutoSmeltEnchant());
		this.addModule(new ModuleFortunate());

		this.addListener(new Listener(this)
		{
			final ItemStack proxyStack = ObjectUtils.newInstance(ItemStack.class);
			final Map<Material, Map<Byte, ItemStack>> dropMap = new EnumishMap<>(Material.length());

			@Handler(ignoreCancelled = true)
			public void onBreak(BlockBreakEvent event)
			{
				if (event.getHand() == null) return;

				Block block = event.getBlock();

				Material type = block.getType();

				Map<Byte, ItemStack> dataMap = this.dropMap.get(type);

				if (dataMap == null)
				{
					dataMap = Maps.newHashMap();

					this.dropMap.put(type, dataMap);
				}

				Byte data = block.getData();

				ItemStack stack = dataMap.get(data);

				if (stack == null)
				{
					List<ItemStack> drops = block.getDrops();

					stack = ListUtils.firstValue(drops);

					if (stack == null)
					{
						dataMap.put(data, this.proxyStack);

						return;
					}

					dataMap.put(data, stack);
				}
				else if (stack == this.proxyStack)
				{
					return;
				}

				event.setCustomItem(stack.copy());
			}
		});
	}

}