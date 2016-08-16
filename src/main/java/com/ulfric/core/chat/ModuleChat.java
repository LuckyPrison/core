package com.ulfric.core.chat;
import com.ulfric.lib.coffee.module.Module;

public class ModuleChat extends Module {

	public ModuleChat()
	{
		super("chat", "Responsible for all other chat-related things", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addModule(new ModuleChatToggler());
	}

}