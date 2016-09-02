package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.entity.player.GameMode;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModuleGameModeInterface extends Module {

	public ModuleGameModeInterface()
	{
		super("game-mode-interface", "/gamemode", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandGameMode());
	}

	private class CommandGameMode extends Command
	{
		public CommandGameMode()
		{
			super("gamemode", ModuleGameModeInterface.this, "gm");

			this.addPermission("gamemode.use");

			this.addOptionalArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setPermission("gamemode.others").build());
			this.addArgument(Argument.builder().setPath("gamemode").addSimpleResolver(GameMode::of).addSimpleResolver(str ->
			{
				Integer value = NumberUtils.parseInteger(str);

				if (value == null) return null;

				return GameMode.byId(value);
			}).setUsage("gamemode-specify-gamemode").build());
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			Player player = (Player) this.getObject("player");

			if (player == null)
			{
				if (!(sender instanceof Player))
				{
					sender.sendLocalizedMessage("gamemode-specify-player");

					return;
				}

				player = (Player) sender;
			}

			GameMode mode = (GameMode) this.getObject("gamemode");

			GameMode current = player.getGameMode();

			if (mode.equals(current))
			{
				if (player == sender)
				{
					sender.sendLocalizedMessage("gamemode-already-mode", mode.getFormattedName());

					return;
				}

				sender.sendLocalizedMessage("gamemode-already-mode-other", player.getName(), mode.getFormattedName());

				return;
			}

			player.setGameMode(mode);

			if (player == sender) return;

			sender.sendLocalizedMessage("gamemode-changed-other", player.getName(), mode.getFormattedName());
		}
	}

}