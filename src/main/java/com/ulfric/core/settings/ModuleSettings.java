package com.ulfric.core.settings;

import com.ulfric.data.DocumentStore;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModuleSettings extends Module {

	public ModuleSettings()
	{
		super("settings", "/settings", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandSettings(this));

		DocumentStore database = PlayerUtils.getPlayerData();

		database.ensureTableCreated("settings");
	}

}