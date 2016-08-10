package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModuleClearChat extends Module {

	public ModuleClearChat()
	{
		super("clear-chat", "/clearchat", "1.0.0", "Packet");
	}

	int lines;

	@Override
	public void onModuleEnable()
	{
		this.lines = Math.abs(this.getModuleConfig().getRoot().getInteger("lines", 100));
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new Command("clearchat", this, "cc", "clearc", "cchat")
		{
			@Override
			public void run()
			{
				String name = this.getSender().getName();
				int lineCount = ModuleClearChat.this.lines;

				for (Player player : PlayerUtils.getOnlinePlayers())
				{
					if (player.hasPermission("clearchat.bypass"))
					{
						player.sendLocalizedMessage("clearchat.bypass", lineCount);

						player.sendLocalizedMessage("clearchat.cleared", name);

						continue;
					}

					for (int x = 0; x < lineCount; x++)
					{
						player.sendMessage("");
					}

					player.sendLocalizedMessage("clearchat.cleared", name);
				}
			}
		}.addPermission("clearchat.use"));
	}

}