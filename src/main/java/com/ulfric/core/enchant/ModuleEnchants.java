package com.ulfric.core.enchant;

import java.util.Set;

import org.apache.commons.lang.Validate;

import com.google.common.collect.Sets;
import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.location.VectorPattern;
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
		this.enchants = Sets.newHashSet();
		this.addCommand(new CommandEnchant(this));

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

				Set<Vector> vectors = null;

				for (Enchant enchant : enchs.getAll())
				{
					Enchantment ench = enchant.getEnchantment();

					if (ench instanceof VectorPatternEnchantment)
					{
						if (vectors == null)
						{
							vectors = Sets.newHashSet();
						}

						VectorPatternEnchantment pattern = (VectorPatternEnchantment) ench;

						pattern.getPattern().transform(location, vectors);

						continue;
					}
				}

				if (vectors == null) return;

				World world = location.getWorld();

				for (Vector vector : vectors)
				{
					Block toBreak = world.getBlock(vector);

					if (toBreak.getTypeOrdinal() == 0) continue;

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

			Enchantment enchant = null;

			if (type.equals("vector-pattern"))
			{
				VectorPattern pattern = VectorPattern.fromDocument(document.getDocument("vector-pattern"));

				enchant = VectorPatternEnchantment.newEnchantment(name, id, max, pattern);
			}
			else
			{
				enchant = Enchantment.newEnchantment(name, id, max);
			}

			Validate.notNull(enchant);

			Validate.isTrue(this.enchants.add(enchant));

			enchant.register();
		}
	}

	@Override
	public void onModuleDisable()
	{
		this.enchants.forEach(Enchantment::unregister);
	}

}