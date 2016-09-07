package com.ulfric.core.shutdown;

import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.event.Priority;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.AsyncPreLoginEvent;
import com.ulfric.lib.craft.event.player.AsyncPreLoginEvent.Result;
import com.ulfric.lib.craft.server.ServerUtils;

final class CommandShutdown extends Command {

	public CommandShutdown(ModuleShutdown owner)
	{
		super("shutdown", owner);

		this.addPermission("shutdown.use");
	}

	private boolean shuttingDown;

	@Override
	public synchronized void run()
	{
		if (this.shuttingDown) return;

		this.shuttingDown = true;

		String message = Locale.getDefault().getRawMessage("shutdown-whitelist");
		this.getOwner().addListener(new Listener(this.getOwner())
		{
			@Handler(priority = Priority.LOWEST)
			public void login(AsyncPreLoginEvent event)
			{
				event.setKickMessage(message);
				event.setResult(Result.KICK_OTHER);
			}
		});

		for (Player player : PlayerUtils.getOnlinePlayers())
		{
			player.kick(player.getLocalizedMessage("shutdown-kick"));
		}

		ThreadUtils.runAsync(() ->
		{
			while (!PlayerScopes.ONLINE.isEmpty())
			{
				try
				{
					Thread.sleep(50L);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			ThreadUtils.run(ServerUtils::shutdown);
		});
	}

}
