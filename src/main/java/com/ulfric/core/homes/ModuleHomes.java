package com.ulfric.core.homes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.ImmutableDocument;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DataContainer;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.function.FunctionUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public class ModuleHomes extends Module {

	private final Map<UUID, List<Home>> cache = Maps.newHashMap();
	private MultiSubscription<UUID, Document> subscription;

	public ModuleHomes()
	{
		super("homes", "Player homes and management", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		DocumentStore database = PlayerUtils.getPlayerData();

		DataManager.get().ensureTableCreated(database, "homes");

		this.subscription = database.multi(
				Document.class, PlayerScopes.ONLINE, new DataAddress<>("homes", "data")
		).blockOnSubscribe(true).blockOnUnsubscribe(true).subscribe();

		this.addCommand(new CommandHome(this));
		this.addCommand(new CommandSethome(this));
		this.addCommand(new CommandDelhome(this));
		this.addCommand(new CommandHomes(this));
	}

	@Override
	public void onModuleDisable()
	{
		this.subscription.unsubscribe();
	}

	public List<Home> getHomes(OfflinePlayer owner)
	{
		List<Home> homes = this.cache.get(owner.getUniqueId());

		if (homes != null)
		{
			return homes;
		}

		homes = Lists.newArrayList();

		Document document = this.subscription.subscribeToForeign(owner.getUniqueId(), FunctionUtils.self()).getValue();

		for (String key : document.getKeys())
		{
			homes.add(Home.fromDocument(owner, document, key));
		}

		this.cache.put(owner.getUniqueId(), homes);

		return homes;
	}

	public Home getHome(OfflinePlayer owner, String homeName)
	{
		return this.getHomes(owner).stream().filter(home -> home.getName().equalsIgnoreCase(homeName)).findFirst().orElse(null);
	}

	public String getDefault(OfflinePlayer owner)
	{
		List<Home> homes = this.getHomes(owner);

		return homes.stream().filter(home -> home.getName().equalsIgnoreCase("home")).map(Home::getName).findFirst().orElseGet(() ->
		{
			if (homes.size() == 1)
			{
				return homes.get(0).getName();
			}
			return "";
		});
	}

	public void save(Home home)
	{
		List<Home> homes = this.getHomes(home.getOwner());

		homes.add(home);

		DataContainer<UUID, Document> container = this.subscription.get(home.getOwner().getUniqueId());

		MutableDocument mut = new SimpleDocument();

		homes.forEach(homeInto -> homeInto.into(mut));

		container.setValue(mut);
	}

	public void delete(Home home)
	{
		this.getHomes(home.getOwner()).remove(home);

		DataContainer<UUID, Document> container = this.subscription.subscribeToForeign(home.getOwner().getUniqueId(), FunctionUtils.self());

		Document document = container.getValue();

		Map<String, Object> copy = document.deepCopy();

		copy.remove(home.getName().toLowerCase());

		ImmutableDocument mut = new ImmutableDocument(copy);

		container.setValue(mut);
	}

}
