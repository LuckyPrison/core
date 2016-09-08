package com.ulfric.core.backpack;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;

public class CommandBackpack extends Command {

	private final ModuleBackpack base;

	public CommandBackpack(ModuleBackpack base)
	{
		super("backpack", base, "bp", "pack");

		this.base = base;

		this.addPermission("core.backpack");

		this.addEnforcer(Enforcers.IS_PLAYER, "backpacks-must-be-player");

		this.addOptionalArgument(OfflinePlayer.ARGUMENT);
		this.addOptionalArgument(Argument.builder().addResolver(Resolvers.INTEGER).setPath("page").setDefaultValue(1).build());
	}

	@Override
	public void run()
	{
		OfflinePlayer owner = (OfflinePlayer) this.getObject(OfflinePlayer.ARGUMENT.getPath());

		if (owner == null)
		{
			owner = (Player) this.getSender();
		}

		Backpack backpack = this.base.getBackpack(owner);

		backpack.checkLimit();

		int page = (int) this.getObject("page");

		if (!backpack.inBounds(page))
		{
			this.getSender().sendLocalizedMessage("backpacks-invalid-page", page, backpack.getMaxPage());

			return;
		}

		backpack.open((Player) getSender(), page);
	}

}
