package com.ulfric.core.homes;

import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModuleHomes extends Module {

	private MapSubscription<Document> subscription;

	public ModuleHomes()
	{
		super("homes", "Player homes and management", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		DocumentStore database = PlayerUtils.getPlayerData();

		database.ensureTableCreated("homes");

		// Unsure about DataAddress arguments
		this.subscription = database.document(
				new DataAddress<>( /* Collection */ "homes", /* id */ null, /* path */ "homes")
		).blockOnSubscribe(true).subscribe();
	}

}
