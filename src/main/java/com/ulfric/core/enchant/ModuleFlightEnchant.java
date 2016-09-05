package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.GameMode;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerItemHeldEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

final class ModuleFlightEnchant extends Module {

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

				if (newItem == null) return;

				Player player = event.getPlayer();

				if (creative.equals(player.getGameMode())) return;

				Enchantment flight = EnchantmentFlight.INSTANCE;

				if (newItem.enchants().contains(flight))
				{
					player.setCanFly(true);

					return;
				}

				player.setCanFly(false);
			}
		});
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