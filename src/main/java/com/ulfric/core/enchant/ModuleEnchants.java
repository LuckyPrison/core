package com.ulfric.core.enchant;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.core.luckyblocks.LuckyBlock;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.location.ImmutableVector;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.location.VectorPattern;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.persist.FileUtils;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemStack.EnchantList;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.inventory.item.enchant.Enchant;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.world.World;

public final class ModuleEnchants extends Module {

	public ModuleEnchants()
	{
		super("enchants", "Enchants module", "1.0.0", "Packet");
	}

	private Set<Enchantment> enchants;

	@Override
	public void onFirstEnable()
	{
		this.addModule(new ModuleDropModifier());
		this.addModule(new ModuleFlightEnchant());

		this.enchants = Sets.newHashSet();
		this.addCommand(new CommandEnchant(this));

		this.addListener(new EnchantSign(this));

		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onBreak(BlockBreakEvent event)
			{
				Block block = event.getBlock();

				if (block.containsMetadata("fake_break")) return;

				Player player = event.getPlayer();

				ItemStack hand = player.getMainHand();

				if (ItemUtils.isEmpty(hand)) return;

				EnchantList enchs = hand.enchants();

				Location location = block.getLocation();
				int bx = location.getIntX();
				int by = location.getIntY();
				int bz = location.getIntZ();

				Set<Vector> vectors = null;

				for (Enchant enchant : enchs.getAll())
				{
					Enchantment ench = enchant.getEnchantment();
					int level = enchant.getLevel();

					if (ench instanceof VectorPatternEnchantment)
					{
						if (vectors == null)
						{
							vectors = Sets.newHashSet();
						}

						VectorPatternEnchantment pattern = (VectorPatternEnchantment) ench;

						pattern.getPattern(level).transform(bx, by, bz, vectors);
					}

					else if (ench == EnchantmentBlasting.INSTANCE)
					{
						if (vectors == null)
						{
							vectors = Sets.newHashSet();
						}

						Vector defensive = ImmutableVector.of(location);
						int radius = Math.min(level + 2, 6);
						int r = Math.round(radius / 1.5F);
						int max = (int) (level + Math.round((radius) / 1.75));

						for (int i = 0; i < max; i++)
						{
							int x = RandomUtils.nextInt(radius);
							int y = RandomUtils.nextInt(r);
							int z = RandomUtils.nextInt(radius);

							if (RandomUtils.nextBoolean())
							{
								x = -x;
							}

							if (RandomUtils.nextBoolean())
							{
								y = -y;
							}

							if (RandomUtils.nextBoolean())
							{
								z = -z;
							}

							vectors.add(defensive.add(x, y, z));
						}
					}
				}

				if (vectors == null) return;

				World world = location.getWorld();

				for (Vector vector : vectors)
				{
					Block toBreak = world.getBlock(vector);

					if (toBreak.getTypeOrdinal() == 0) continue;

					if (toBreak.getType().getBestTool() == null) continue;

					if (LuckyBlock.isLuckyBlock(toBreak)) continue;

					player.breakBlock(toBreak);
				}
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		String confName = FileUtils.getName(this.getModuleConfig().getPath());
		for (ConfigFile config : this.getModuleConfigs())
		{
			if (FileUtils.getName(config.getPath()).equals(confName)) continue;

			Document document = config.getRoot();

			String name = document.getString("name");
			int id = document.getLong("id").intValue();
			int max = document.getLong("max").intValue();
			String type = document.getString("type", "normal");
			List<Integer> conflicts = document.getIntegerList("conflicts", ImmutableList.of());

			Enchantment enchant = null;

			if (type.equals("vector-pattern"))
			{
				SortedMap<Integer, VectorPattern> map = Maps.newTreeMap();

				document = document.getDocument("vectors");

				for (String key : document.getKeys(false))
				{
					Document vecDoc = document.getDocument(key);

					map.putIfAbsent(vecDoc.getInteger("level"), VectorPattern.fromDocument(vecDoc.getDocument("pattern")));
				}

				enchant = VectorPatternEnchantment.newEnchantment(name, id, max, map, conflicts);
			}
			else
			{
				enchant = Enchantment.newEnchantment(name, id, max, conflicts, null);
			}

			Validate.notNull(enchant);

			Validate.isTrue(this.enchants.add(enchant));

			enchant.register();

			this.log("Loaded enchantment: " + enchant.getName() + " with ID " + enchant.getId());
		}

		EnchantmentBlasting.INSTANCE.register();
	}

	@Override
	public void onModuleDisable()
	{
		this.enchants.forEach(Enchantment::unregister);

		this.enchants.clear();

		EnchantmentBlasting.INSTANCE.unregister();
	}

}