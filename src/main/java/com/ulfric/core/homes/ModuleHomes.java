package com.ulfric.core.homes;

import java.util.UUID;

import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModuleHomes extends Module {

	private MultiSubscription<UUID, Document> subscription;

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
		this.subscription = database.multi(
				Document.class, PlayerScopes.ONLINE, new DataAddress<>("homes", null, "data")
		).blockOnSubscribe(true).subscribe();
	}

	@Override
	public void onModuleDisable()
	{
		this.subscription.unsubscribe();
	}

}
