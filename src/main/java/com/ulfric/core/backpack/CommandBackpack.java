package com.ulfric.core.backpack;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandBackpack extends Command {

	public CommandBackpack(ModuleBase module)
	{
		super("backpack", module, "bp", "pack");

		super.addPermission("core.backpack");

		super.addEnforcer(Enforcers.IS_PLAYER, "backpacks.must_be_player");

		super.addOptionalArgument(OfflinePlayer.ARGUMENT);
		super.addOptionalArgument(Argument.builder().addResolver(Resolvers.INTEGER).setPath("page").setDefaultValue(1).build());
	}

	@Override
	public void run()
	{
		OfflinePlayer owner = (OfflinePlayer) super.getObject(OfflinePlayer.ARGUMENT);

		if (owner == null)
		{
			owner = (Player) super.getSender();
		}

		Backpack backpack = ModuleBackpack.getInstance().getBackpack(owner);

		int page = (int) super.getObject("page");

		if (!backpack.inBounds(page))
		{
			super.getSender().sendLocalizedMessage("backpacks.invalid_page", page);

			return;
		}

		backpack.open((Player) getSender(), page);
	}

}
