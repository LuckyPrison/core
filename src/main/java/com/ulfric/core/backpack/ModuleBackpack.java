package com.ulfric.core.backpack;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

import java.util.Map;
import java.util.UUID;

public final class ModuleBackpack extends Module {

	private static ModuleBackpack instance;

	public static ModuleBackpack getInstance()
	{
		return instance;
	}

	// TODO: Resolve
//	private static final Argument PAGE = Argument.builder().addSimpleResolver(NumberUtils::parseInteger).setPath("page").setDefaultValue(1).build();
	// TODO: Resolve
//	private static final Argument PLAYER = OfflinePlayer.ARGUMENT;

	private MultiSubscription<UUID, Document> subscription;
	private final Map<UUID, Backpack> cache = Maps.newHashMap();

	public ModuleBackpack()
	{
		super("backpack", "Backpack command module", "1.0.0", "[feildmaster, insou]");
		if (instance != null)
		{
			throw new IllegalStateException("Already initialized!");
		}
		instance = this;
	}

	@Override
	public void onFirstEnable()
	{
		DocumentStore database = PlayerUtils.getPlayerData();

		this.subscription = database
				.multi(Document.class, PlayerScopes.ONLINE, new DataAddress<>("backpacks", null, null))
				.blockOnSubscribe(true)
				.subscribe();

		this.addCommand(new CommandBackpack(this));
	}

	@Override
	public void onModuleDisable()
	{
		this.subscription.unsubscribe();
	}

	public MultiSubscription<UUID, Document> getSubscription()
	{
		return subscription;
	}

	protected Backpack getBackpack(OfflinePlayer player)
	{
		Backpack cached = this.cache.get(player.getUniqueId());

		if (cached != null)
		{
			return cached;
		}

		Document document = this.subscription.get(player.getUniqueId()).getValue();

		if (document == null || document.getKeys().size() == 0)
		{
			return this.cache.put(player.getUniqueId(), this.makeBackpack(player));
		}

		return this.cache.put(player.getUniqueId(), Backpack.fromDocument(player, document));
	}

	private Backpack makeBackpack(OfflinePlayer owner)
	{
		Backpack backpack = new Backpack(owner, 1, 1);

		backpack.save();

		return backpack;
	}

}
