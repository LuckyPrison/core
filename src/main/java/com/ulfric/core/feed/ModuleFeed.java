package com.ulfric.core.feed;

import com.ulfric.lib.coffee.module.Module;

public class ModuleFeed extends Module {

	public ModuleFeed()
	{
		super("feed", "feed command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		super.addCommand(new CommandFeed(this));
	}

}
