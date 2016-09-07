package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.GameMode;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerItemHeldEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

public final class ModuleFlightEnchant extends Module {

	public static final ModuleFlightEnchant INSTANCE = new ModuleFlightEnchant();

	public ModuleFlightEnchant()
	{
		super("flight-enchant", "Flying with a pickaxe", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		GameMode creative = GameMode.of("CREATIVE");
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onHold(PlayerItemHeldEvent event)
			{
				ItemStack newItem = event.getNewItem();

				Player player = event.getPlayer();

				if (creative.equals(player.getGameMode())) return;

				if (newItem != null)
				{
					if (ModuleFlightEnchant.this.enchantPresent(player, newItem))
					{
						player.setCanFly(true);
					}
					else
					{
						if (!player.containsMetadata("flight") || !player.getMetadataAsBoolean("flight"))
						{
							player.setCanFly(false);
						}
					}
				}
			}
		});
	}

	public boolean enchantPresent(Player player)
	{
		return this.enchantPresent(player, player.getMainHand());
	}

	private boolean enchantPresent(Player player, ItemStack newItem)
	{
		Enchantment flight = EnchantmentFlight.INSTANCE;

		return newItem.enchants().contains(flight);

	}

	@Override
	public void onModuleEnable()
	{
		EnchantmentFlight.INSTANCE.register();
	}

	@Override
	public void onModuleDisable()
	{
		EnchantmentFlight.INSTANCE.unregister();
	}

}