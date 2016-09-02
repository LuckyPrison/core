package com.ulfric.core.permissions;

import com.ulfric.lib.coffee.module.Module;

public final class ModulePermissionInterface extends Module {

	public ModulePermissionInterface()
	{
		super("permission-interface", "Module for managing permissions", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandPermissions(this));
	}

}