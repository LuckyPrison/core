package com.ulfric.core.gangs;

import java.util.List;
import java.util.UUID;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.string.Joiner;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class SubCommandInvites extends GangCommand {

	public SubCommandInvites(ModuleBase owner)
	{
		super("invites", null, owner);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Gang gang = this.getGang();

		List<UUID> list = gang.getInvites();

		if (list.isEmpty())
		{
			sender.sendLocalizedMessage("gangs.invites_none");

			return;
		}

		Locale locale = sender.getLocale();

		Joiner joiner = Joiner.lineBreak();

		joiner.append(locale.getFormattedMessage("gangs.invites_header", gang.getName(), list.size()));
		// TODO more info, also allow verbose info

		String raw = locale.getRawMessage("gangs.invites_entry");

		for (UUID uuid : list)
		{
			OfflinePlayer player = PlayerUtils.getOfflinePlayer(uuid);

			String name;

			if (player == null)
			{
				name = uuid.toString() + " (UUID)";
			}

			else
			{
				name = player.getName();

				if (name == null)
				{
					name = uuid.toString() + " (UUID)";
				}
			}

			joiner.append(Strings.format(raw, name));
		}

		sender.sendMessage(joiner.toString());
	}

}