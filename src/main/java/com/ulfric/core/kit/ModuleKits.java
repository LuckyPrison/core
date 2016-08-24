package com.ulfric.core.kit;

import java.util.Set;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.module.Module;

public final class ModuleKits extends Module {

	public ModuleKits()
	{
		super("kits", "/kits", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandKit(this));
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile conf = this.getModuleConfig();

		Document doc = conf.getRoot().getDocument("kits");

		if (doc == null) return;

		Set<String> keys = doc.getKeys(false);

		if (SetUtils.isEmpty(keys)) return;

		for (String key : keys)
		{
			Document keyDoc = doc.getDocument(key);

			if (keyDoc == null) continue;

			Kit kit = Kit.fromDocument(keyDoc);

			if (kit == null) continue;

			Kits.INSTANCE.registerKit(kit);
		}
	}

	@Override
	public void onModuleDisable()
	{
		Kits.INSTANCE.clear();
	}

}