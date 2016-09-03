package com.ulfric.core.combattag;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

class CommandUnCombatTag extends Command {

	CommandUnCombatTag(ModuleBase module)
	{
		super("uncombattag", module, "unct", "uct", "untag", "utag");

		this.addArgument(Argument.builder().setPath("player").addResolver(PlayerUtils::getOnlinePlayer).setDefaultValue(cmd -> PlayerUtils.getPlayer(cmd.getSender())).build());

		this.addPermission("uncombattag.use");
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();
		Player player = (Player) this.getObject("player");

		CombatTag tag = Tags.INSTANCE.removeTag(player.getUniqueId());

		if (tag == null)
		{
			if (player.getUniqueId().equals(sender.getUniqueId()))
			{
				sender.sendLocalizedMessage("combattag-remove-self-not-tagged");

				return;
			}

			sender.sendLocalizedMessage("combattag-remove-other-not-tagged", player.getName());

			return;
		}

		if (player.getUniqueId().equals(sender.getUniqueId()))
		{
			sender.sendLocalizedMessage("combattag-remove-self");

			return;
		}

		player.sendLocalizedMessage("combattag-removed-by", sender.getName());
		sender.sendLocalizedMessage("combattag-remove-other", player.getName());
	}

}