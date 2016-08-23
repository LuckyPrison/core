package com.ulfric.core.rankup;
import org.apache.commons.lang3.StringUtils;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.permission.Group;
import com.ulfric.lib.coffee.permission.PermissionsManager;

public class ModuleRankup extends Module {

	public ModuleRankup()
	{
		super("rankup", "Rankups module", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandRankup(this));
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = this.getModuleConfig();

		Document document = config.getRoot();

		Document ranksDoc = document.getDocument("rankups");

		if (ranksDoc != null)
		{
			PermissionsManager manager = PermissionsManager.get();

			for (String key : ranksDoc.getKeys(false))
			{
				Document rankDoc = ranksDoc.getDocument(key);

				if (rankDoc == null) continue;

				Group group = manager.getGroup(rankDoc.getString("group", key));

				if (group == null) continue;

				String cost = rankDoc.getString("price");
				CurrencyAmount price = StringUtils.isBlank(cost) ? null : CurrencyAmount.valueOf(cost);

				Rankups.INSTANCE.registerRankup(group, price);
			}
		}
	}

	@Override
	public void onModuleDisable()
	{
		Rankups.INSTANCE.clear();
	}

}