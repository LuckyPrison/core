package com.ulfric.core.modules;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModulePrivateMessaging extends Module {

	public ModulePrivateMessaging()
	{
		super("private-messaging", "/message (/msg), /reply (/r)", "1.0.0", "Packet");
	}

	Map<UUID, UUID> messagers;

	@Override
	public void onFirstEnable()
	{
		this.messagers = new LRUMap<>(1000);

		this.addCommand(new CommandMessage());
		this.addCommand(new CommandReply());
	}

	@Override
	public void onModuleDisable()
	{
		this.messagers.clear();
	}

	private class CommandMessage extends Command
	{
		public CommandMessage()
		{
			super("message", ModulePrivateMessaging.this, "msg", "m", "wisper", "w", "tell");

			this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).build());
		}

		@Override
		public void run()
		{
			String message = this.buildUnusedArgs();

			if (StringUtils.isBlank(message))
			{
				message = ".";
			}

			CommandSender sender = this.getSender();
			Player target = (Player) this.getObject("player");

			if (target.getUniqueId().equals(sender.getUniqueId()))
			{
				sender.sendLocalizedMessage("message.self", message);

				return;
			}

			String targetName = target.getName();
			String senderName = sender.getName();

			sender.sendLocalizedMessage("message.sent", targetName, message);
			target.sendLocalizedMessage("message.received", senderName, message);

			ModulePrivateMessaging.this.messagers.put(sender.getUniqueId(), target.getUniqueId());
			ModulePrivateMessaging.this.messagers.put(target.getUniqueId(), sender.getUniqueId());

			for (Player allPlayers : PlayerUtils.getOnlinePlayers())
			{
				if (!allPlayers.hasSocialSpy()) continue;

				allPlayers.sendLocalizedMessage("message.socialspy", senderName, targetName, message);
			}
		}
	}

	private class CommandReply extends Command
	{
		public CommandReply()
		{
			super("reply", ModulePrivateMessaging.this, "rep", "r");
		}

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			if (!ModulePrivateMessaging.this.messagers.containsKey(sender.getUniqueId()))
			{
				sender.sendLocalizedMessage("reply.no_message_sent");

				return;
			}

			UUID targetUUID = ModulePrivateMessaging.this.messagers.get(sender.getUniqueId());

			Player target = PlayerUtils.getOnlinePlayer(sender, targetUUID);

			if (target == null && targetUUID != null)
			{
				sender.sendLocalizedMessage("reply.recipient_offline");

				return;
			}

			String message = this.buildUnusedArgs();

			if (StringUtils.isBlank(message))
			{
				message = ".";
			}

			String name = sender.getName();

			sender.sendLocalizedMessage("message.sent", target == null ? sender.getLocalizedMessage("system.console") : target.getName(), message);

			if (target == null)
			{
				ModulePrivateMessaging.this.log(Locale.getDefault().getFormattedMessage("message.received", name, message));

				return;
			}

			String targetName = target.getName();

			for (Player allPlayers : PlayerUtils.getOnlinePlayers())
			{
				if (!allPlayers.hasSocialSpy()) continue;

				allPlayers.sendLocalizedMessage("message.socialspy", name, targetName, message);
			}

			target.sendLocalizedMessage("message.received", name, message);
		}
	}

}