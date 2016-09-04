package com.ulfric.core.control;

import java.util.Collection;

import com.ulfric.core.combattag.CombatTag;
import com.ulfric.core.combattag.Tags;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.string.WordUtils;
import com.ulfric.lib.craft.entity.player.Player;

class CommandClearInventory extends Command {

	public CommandClearInventory(ModuleBase owner)
	{
		super("clearinventory", owner, "clearinv");

		this.addArgument(Argument.builder().setPath("holder").addResolver(PunishmentHolder.ARGUMENT).setDefaultValue(cmd ->
		{
			CommandSender sender = this.getSender();

			if (!(sender instanceof Player)) return null;

			return Punishments.getInstance().getHolder(sender.getUniqueId());
		}).setPermission("clearinventory.other").build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		PunishmentHolder holder = (PunishmentHolder) this.getObject(PunishmentHolder.ARGUMENT.getPath());

		if (sender.getUniqueId().equals(holder.getUniqueId()))
		{
			Player player = (Player) sender;

			CombatTag tag = Tags.INSTANCE.getTag(player.getUniqueId());

			if (tag != null)
			{
				sender.sendLocalizedMessage("clearinventory.self_combat_tagged");

				return;
			}

			player.clearInventory();

			sender.sendLocalizedMessage("clearinventory.self");

			return;
		}

		Collection<Player> players = holder.toPlayers();

		if (players.isEmpty())
		{
			sender.sendLocalizedMessage("control.specify_holder");

			return;
		}

		int size = players.size();

		if (size == 1)
		{
			sender.sendLocalizedMessage("clearinventory.cleared_single", WordUtils.merge(players));
		}
		else
		{
			sender.sendLocalizedMessage("clearinventory.cleared_multi", size, WordUtils.merge(players));
		}

		players.forEach(Player::clearInventory);
	}

}