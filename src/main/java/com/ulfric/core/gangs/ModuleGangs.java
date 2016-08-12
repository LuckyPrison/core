package com.ulfric.core.gangs;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.module.Module;

public class ModuleGangs extends Module {

	public ModuleGangs()
	{
		super("gangs", "Gangs, parties, guilds, they're all the same", "1.0.0", "Packet");
	}

	private MapSubscription<Document> subscription;

	@Override
	public void onFirstEnable()
	{
		DataManager manager = DataManager.get();
		DocumentStore database = manager.getEnsuredDatabase("gangs");

		manager.ensureTableCreated(database, "gangs");

		this.subscription = database.document(new DataAddress<>("gangs", "gangs", null)).blockOnSubscribe(true).subscribe();

		Gangs.getInstance().setSubscription(this.subscription);

		this.addCommand(new CommandGangs(this));
	}

	@Override
	public void onModuleEnable()
	{
		this.subscription.subscribe();

		Document document = this.subscription.getValue();

		Set<String> keys = document.getKeys(false);

		if (keys == null || keys.isEmpty())
		{
			this.log("No gang data found");

			return;
		}

		this.log("Loading data for " + keys.size() + " gangs");

		Gangs gangs = Gangs.getInstance();

		List<String> delete = Lists.newArrayList();

		for (String key : keys)
		{
			Document gangDocument = document.getDocument(key);

			if (gangDocument == null)
			{
				this.log("[WARNING] Not a gang: " + key);

				continue;
			}

			Gang gang = Gang.fromDocument(gangDocument);

			if (gang == null)
			{
				this.log("[WARNING] Could not load gang: " + key);

				this.log("Failed document: " + gangDocument);

				delete.add(key);

				continue;
			}

			gangs.registerGang(gang);
		}

		if (delete.isEmpty()) return;

		this.log("Deleting " + delete.size() + " invalid keys");

		for (String deleteKey : delete)
		{
			this.subscription.removeField(deleteKey);

			this.log("Deleted: " + deleteKey);
		}
	}

	@Override
	public void onModuleDisable()
	{
		this.subscription.unsubscribe();
	}

}