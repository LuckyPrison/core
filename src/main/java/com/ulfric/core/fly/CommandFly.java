package com.ulfric.core.fly;

import com.ulfric.core.enchant.EnchantmentFlight;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;

final class CommandFly extends Command {

	CommandFly(ModuleFly owner)
	{
		super("fly", owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "fly-is-not-player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		if (player.canFly())
		{
			player.removeMetadata("flight");

			ItemStack item = player.getMainHand();

			if (item != null)
			{
				if (item.enchants().contains(EnchantmentFlight.INSTANCE))
				{
					player.setFlying(false);

					return;
				}
			}

			player.setCanFly(false);

			return;
		}

		player.setCanFly(true);

		player.markMetadata("flight");
	}

}
