package com.ulfric.core.economy;
import com.ulfric.lib.coffee.module.Module;

public class ModuleEconomyInterface extends Module {

	public ModuleEconomyInterface()
	{
		super("economy-interface", "Economy interfacing; the pay command, balance signs, etc.", "1.0.0", "Packet and evilmidget38");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandBalance(this));
		this.addCommand(new CommandPay(this));

		this.addListener(new SignBalance(this));
	}

}