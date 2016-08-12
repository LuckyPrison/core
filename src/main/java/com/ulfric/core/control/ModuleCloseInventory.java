package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.string.ChatUtils;

public class ModuleCloseInventory extends Module {

	public ModuleCloseInventory()
	{
		super("close-inventory", "/closeinventory", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandCloseInventory());
	}

	private final class CommandCloseInventory extends Command
	{
		CommandCloseInventory()
		{
			super("closeinventory", ModuleCloseInventory.this, "closeinv");

			this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).build());

			this.addPermission("closeinventory.use");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getObject("player");

			String title = player.getInventory().getName();

			player.closeInventory();

			this.getSender().sendLocalizedMessage("closeinventory.closed", player.getName(), ChatUtils.stripColor(title));
		}
	}

}