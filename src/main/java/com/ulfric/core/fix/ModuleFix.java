package com.ulfric.core.fix;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.module.Module;

public class ModuleFix extends Module {

	private long cost;
	private Currency currency;

	private CurrencyAmount amount;

	public ModuleFix()
	{
		super("fix", "/fix command", "1.0.0", "insou");
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = super.getModuleConfig();
		MutableDocument document = config.getRoot();
		if (!document.contains("cost"))
		{
			document.set("cost", CurrencyAmount.of(Currency.getDefaultCurrency(), 100L).toString());
			config.save();
		}
		this.amount = CurrencyAmount.valueOf(document.getString("cost"));

		super.addCommand(new CommandFix(this));
	}

	public CurrencyAmount getAmount()
	{
		return amount;
	}

}
