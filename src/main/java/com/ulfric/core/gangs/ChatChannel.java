package com.ulfric.core.gangs;

import com.ulfric.lib.coffee.enums.EnumUtils;

enum ChatChannel {

	GLOBAL,
	GANG,
	ALLY;

	public static ChatChannel getNext(ChatChannel start)
	{
		if (start == null) return ChatChannel.GANG;

		if (start == ChatChannel.GANG) return ChatChannel.ALLY;

		if (start == ChatChannel.ALLY) return ChatChannel.GLOBAL;

		return ChatChannel.GANG;
	}

	public static ChatChannel parseChannel(String context)
	{
		if (context == null) return ChatChannel.GLOBAL;

		String upper = context.toUpperCase();

		if (upper.equals("PUBLIC")) return ChatChannel.GLOBAL;

		return EnumUtils.valueOf(context, ChatChannel.class, 2);
	}

}