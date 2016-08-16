package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;

final class SubCommandChat extends GangCommand {

	public SubCommandChat(ModuleBase owner)
	{
		super("chat", GangRank.MEMBER, owner);

		this.addArgument(Argument.builder().setPath("channel").addSimpleResolver(ChatChannel::parseChannel).setDefaultValue(cmd ->
		{
			Player player = (Player) this.getSender();

			ChatChannel channel = player.getMetadataAs("gang_channel", ChatChannel.class);

			return ChatChannel.getNext(channel);
		}).build());

		this.addEnforcer(Enforcers.IS_PLAYER, "gangs.chat_must_be_player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		ChatChannel currentChannel = player.getMetadataAs("gang_channel", ChatChannel.class);

		ChatChannel channel = (ChatChannel) this.getObject("channel");

		if (channel == currentChannel)
		{
			player.sendLocalizedMessage("gangs.chat_channel_already", EnumUtils.format(channel));

			return;
		}

		player.setMetadata("gang_channel", channel);

		player.sendLocalizedMessage("gangs.chat_channel_set", EnumUtils.format(channel));
	}

}