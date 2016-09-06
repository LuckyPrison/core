package com.ulfric.core.luckyblocks;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.collection.EnumishMap;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.inventory.item.Material;

public final class ModuleLuckyBlocks extends Module {

	public static final ModuleLuckyBlocks INSTANCE = new ModuleLuckyBlocks();

	private ModuleLuckyBlocks()
	{
		super("luckyblocks", "LuckyBlocks!", "1.0.0", "Packet");
	}

	Map<Material, Map<Byte, LuckyBlock>> luckyblocks = new EnumishMap<>(Material.length());

	boolean isLuckyBlock(Block block)
	{
		Map<Byte, LuckyBlock> map = this.luckyblocks.get(block.getType());

		if (map == null) return false;

		return map.get(block.getData()) != null;
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onBlockBreak(BlockBreakEvent event)
			{
				Block block = event.getBlock();

				Map<Byte, LuckyBlock> blocks = ModuleLuckyBlocks.this.luckyblocks.get(block.getType());

				if (blocks == null) return;

				LuckyBlock luckyblock = blocks.get(block.getData());

				if (luckyblock == null) return;

				Player player = event.getPlayer();

				event.setCancelled(true);

				if (!luckyblock.canUse(player))
				{
					player.sendTimedLocalizedMessage(luckyblock.getErrorMillisDelay(), luckyblock.getErrorMessage());

					return;
				}

				block.setType(null);

				luckyblock.use(player, block);
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		Document root = this.getModuleConfig().getRoot();

		Document luckyblocksDocument = root.getDocument("luckyblocks");

		if (luckyblocksDocument == null) return;

		Set<String> keys = luckyblocksDocument.getKeys(false);

		if (SetUtils.isEmpty(keys)) return;

		for (String key : keys)
		{
			Document luckyblockDocument = luckyblocksDocument.getDocument(key);

			if (luckyblockDocument == null) continue;

			String name = luckyblockDocument.getString("name", key);
			MaterialData data = MaterialData.of(luckyblockDocument.getString("material"));

			LuckyBlock.Builder builder = LuckyBlock.builder();

			builder.setName(name);
			builder.setType(data);

			Document permissionDocument = luckyblockDocument.getDocument("permissions");

			if (permissionDocument != null)
			{
				String node = permissionDocument.getString("node");
				builder.setPermission(node);

				Document errorDocument = permissionDocument.getDocument("error");
				if (errorDocument != null)
				{
					String error = errorDocument.getString("message");
					long millisDelay = errorDocument.getLong("delay");

					builder.setPermissionError(error);
					builder.setPermissionErrorDelayInMillis(millisDelay);
				}
			}

			LuckyBlock luckyblock = builder.build();

			Material material = data.getMaterial();

			Map<Byte, LuckyBlock> dataValues = this.luckyblocks.get(material);

			if (dataValues == null)
			{
				dataValues = Maps.newHashMap();

				this.luckyblocks.put(material, dataValues);
			}

			dataValues.put(data.getDataByte(), luckyblock);
		}
	}

	@Override
	public void onModuleDisable()
	{
		this.luckyblocks.clear();
	}

}