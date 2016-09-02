package com.ulfric.core.kit;

import java.util.concurrent.TimeUnit;

import com.ulfric.core.gangs.Gang;
import com.ulfric.core.gangs.GangMember;
import com.ulfric.core.gangs.Gangs;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.time.TimeUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

final class CommandKit extends Command {

	public CommandKit(ModuleBase owner)
	{
		super("kit", owner);

		this.addArgument(Argument.builder().setPath("kit").addResolver((sen, str) ->
		{
			Kit kit = Kits.INSTANCE.getByName(str);

			if (kit == null) return null;

			if (!sen.hasPermission("kits." + kit.getName())) return null;

			return kit;
		}).setUsage("kit-specify-kit").build());

		this.addArgument(Argument.builder().setPath("player").setDefaultValue(cmd -> cmd.getSender()).addResolver((sen, str) ->
		{
			Player found = PlayerUtils.getOnlinePlayer(sen, str);

			if (found == null) return null;

			if (sen.hasPermission("kit.all")) return found;

			Gangs gangs = Gangs.getInstance();

			GangMember member = gangs.getMember(sen.getUniqueId());

			if (member == null) return null;

			Gang gang = member.getGang();

			member = gang.getMember(found.getUniqueId());

			if (member == null) return null;

			return found;
		}).build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Object rawPlayer = this.getObject("player");

		if (!(rawPlayer instanceof Player)) return;

		Player player = (Player) rawPlayer;

		Kit kit = (Kit) this.getObject("kit");

		final boolean same = sender == player;

		long remaining = kit.give(sender.getUniqueId(), player, same ? "Kit usage" : "Kit from " + sender.getName());

		if (remaining == 0)
		{
			if (same)
			{
				sender.sendLocalizedMessage("kit-used-kit", kit.getName());

				return;
			}

			sender.sendLocalizedMessage("kit-used-kit-other", kit.getName(), player.getName());

			player.sendLocalizedMessage("kit-received", kit.getName(), sender.getName());

			return;
		}

		sender.sendLocalizedMessage("kit-cooldown", kit.getName(), TimeUtils.formatMillis(remaining, TimeUnit.MILLISECONDS));
	}

}
