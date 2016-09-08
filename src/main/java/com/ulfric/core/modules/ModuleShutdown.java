package com.ulfric.core.modules;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.event.Priority;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.AsyncPreLoginEvent;
import com.ulfric.lib.craft.server.ServerUtils;

public final class ModuleShutdown extends Module {

	public ModuleShutdown()
	{
		super("shutdown", "Shutdown command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandShutdown());
	}

	private final class CommandShutdown extends Command {

		CommandShutdown()
		{
			super("shutdown", ModuleShutdown.this);

			this.addPermission("shutdown.use");
		}

		private AtomicBoolean shuttingDown = new AtomicBoolean(false);

		@Override
		public synchronized void run()
		{
			if (this.shuttingDown.get()) return;

			this.shuttingDown.set(true);

			String message = Locale.getDefault().getRawMessage("shutdown-whitelist");
			this.getOwner().addListener(new Listener(this.getOwner())
			{
				@Handler(priority = Priority.LOWEST)
				public void login(AsyncPreLoginEvent event)
				{
					event.setKickMessage(message);
					event.setResult(AsyncPreLoginEvent.Result.KICK_OTHER);
				}
			});

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

			for (Player player : PlayerUtils.getOnlinePlayers())
			{
				player.kick(player.getLocalizedMessage("shutdown-kick"));
			}
		}

	}


}
