package com.ulfric.core.enchant;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.enchant.Enchant;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;

final class CommandEnchant extends Command {

	public CommandEnchant(ModuleBase owner)
	{
		super("enchant", owner);

		this.addArgument(Argument.builder().setPath("enchantment").addSimpleResolver(Enchantment::parse).setUsage("enchant.specify_enchantment").build());
		this.addArgument(Argument.builder().setPath("level").addResolver(Resolvers.INTEGER).setUsage("enchant.specify_level").build());

		this.addPermission("enchant.use");

		this.addEnforcer(Enforcers.IS_PLAYER, "enchant.must_be_player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		ItemStack mainHand = player.getMainHand();

		if (mainHand == null) return;

		Enchantment enchantment = (Enchantment) this.getObject("enchantment");
		Integer level = (Integer) this.getObject("level");

		Enchant enchant = Enchant.of(enchantment, level);

		mainHand.enchants().add(enchant);
	}

}