package com.ulfric.core.fix;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.module.Module;

public class ModuleFix extends Module {

	private long cost;
	private Currency currency;

	public ModuleFix()
	{
		super("fix", "/fix command", "1.0.0", "insou");
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = super.getModuleConfig();
		MutableDocument document = config.getRoot();
		if (!document.contains("cost") || !document.contains("currency"))
		{
			if (!document.contains("cost"))
			{
				document.set("cost", 100L);
			}
			if (!document.contains("currency"))
			{
				document.set("currency", Currency.getDefaultCurrency().getName());
			}
			config.save();
		}
		this.cost = document.getLong("cost", 100L);
		this.currency = Currency.getCurrency(document.getString("currency", Currency.getDefaultCurrency().getName()));

		super.addCommand(new CommandFix(this));
	}

	public long getCost()
	{
		return this.cost;
	}

	public Currency getCurrency()
	{
		return currency;
	}

}
