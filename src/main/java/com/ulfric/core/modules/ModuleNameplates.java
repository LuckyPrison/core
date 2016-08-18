package com.ulfric.core.modules;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.Maps;
import com.ulfric.config.ConfigFile;
import com.ulfric.config.MutableDocument;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.panel.Button;
import com.ulfric.lib.craft.panel.Panel;
import com.ulfric.lib.craft.panel.standard.StandardPanel;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardTeam;
import com.ulfric.lib.craft.string.ChatUtils;

public class ModuleNameplates extends Module {

	public ModuleNameplates()
	{
		super("nameplates", "Module for coloring your nameplate", "1.0.0", "Packet");
	}

	Map<String, Nameplate> nameplates;
	Map<UUID, NameplateKey> playerNameplates;
	private MultiSubscription<UUID, String> subscription;
	BiConsumer<Player, Nameplate> consumer;

	@Override
	public void onFirstEnable()
	{
		this.nameplates = Maps.newHashMap();
		this.playerNameplates = Maps.newHashMap();

		DocumentStore db = PlayerUtils.getPlayerData();

		DataManager.get().ensureTableCreated(db, "nameplates");

		this.subscription = db.multi(String.class, PlayerScopes.ONLINE, new DataAddress<>("nameplates", "key"))
						 	  .onChange((oldValue, newValue) ->
						 	  {
						 		  this.playerNameplates.put(newValue.getAddress().getId(), new NameplateKey(newValue.getValue(), false));
						 	  })
						 	  .blockOnSubscribe(true)
						 	  .subscribe();

		Executor executor = Executors.newSingleThreadExecutor();

		this.consumer = (player, plate) ->
		{
			String prefix = plate.getPrefix();
			String name = player.getName();

			for (Player allPlayers : PlayerUtils.getOnlinePlayers())
			{
				if (allPlayers == player) continue;

				Scoreboard scoreboard = allPlayers.getScoreboard();

				ScoreboardTeam team = scoreboard.getTeam(allPlayers, name);

				String currentPrefix = team.getPrefix();

				currentPrefix = currentPrefix == null ? "" : currentPrefix;

				team.setPrefix(currentPrefix + prefix);
			}
		};

		Consumer<Player> youConsumer = player ->
		{
			Scoreboard scoreboard = player.getScoreboard();

			ScoreboardTeam self = scoreboard.getOrCreateTeam(player, "_self");
			self.setPrefix(player.getLocalizedMessage("nameplate.self"));
			self.addEntry(player.getName());
		};

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Player player = event.getPlayer();

				executor.execute(() -> youConsumer.accept(player));

				NameplateKey key = ModuleNameplates.this.playerNameplates.get(player.getUniqueId());

				if (key == null) return;

				Nameplate nameplate = ModuleNameplates.this.nameplates.get(key.getKey());

				if (nameplate == null) return;

				ModuleNameplates.this.consumer.accept(player, nameplate);
			}
		});

		this.addCommand(new CommandNameplates());
	}

	@Override
	public void onModuleEnable()
	{
		if (!this.subscription.isSubscribed())
		{
			this.subscription.subscribe();
		}

		ConfigFile config = this.getModuleConfig();
		MutableDocument document = config.getRoot();

		MutableDocument nameplateDocument = document.getDocument("nameplates");

		if (nameplateDocument == null)
		{
			nameplateDocument = document.createDocument("nameplates");

			MutableDocument exampleDocument = nameplateDocument.createDocument("example");

			exampleDocument.set("item", Material.of("GRASS").toItem().toString());
			exampleDocument.set("prefix", "[EXAMPLE] ");

			config.save();
		}

		for (String key : nameplateDocument.getKeys(false))
		{
			MutableDocument nameplateParse = nameplateDocument.getDocument(key);

			ItemStack item = ItemUtils.getItem(nameplateParse.getString("item"));
			String prefix = ChatUtils.color(nameplateParse.getString("prefix"));

			Nameplate nameplate = new Nameplate(key, item, prefix);

			this.nameplates.put(key, nameplate);
		}
	}

	@Override
	public void onModuleDisable()
	{
		this.subscription.unsubscribe();

		int count = 0;

		for (Entry<UUID, NameplateKey> entry : this.playerNameplates.entrySet())
		{
			NameplateKey value = entry.getValue();

			if (value.isNotModified()) continue;

			count++;

			this.subscription.get(entry.getKey()).setValue(value.getKey());
		}

		if (count > 0)
		{
			if (count == 1)
			{
				this.log("Wrote 1 nameplate change to the database.");
			}
			else
			{
				this.log("Wrote " + count + " nameplate changes to the database.");
			}
		}

		this.nameplates.clear();
		this.playerNameplates.clear();
	}

	private class Nameplate extends NamedBase
	{
		Nameplate(String name, ItemStack item, String prefix)
		{
			super(name);

			this.item = item;
			this.prefix = prefix;
		}

		private final ItemStack item;
		private final String prefix;

		public ItemStack getItem()
		{
			return this.item;
		}

		public String getPrefix()
		{
			return this.prefix;
		}
	}

	private class NameplateKey
	{
		NameplateKey(String key, boolean modified)
		{
			this.key = key;

			this.modified = modified;
		}

		private String key;
		private boolean modified;

		public String getKey()
		{
			return this.key;
		}

		public void setKey(String key)
		{
			this.key = key;

			this.modified = true;
		}

		public boolean isModified()
		{
			return this.modified;
		}

		public boolean isNotModified()
		{
			return !this.isModified();
		}
	}

	private class CommandNameplates extends Command
	{
		public CommandNameplates()
		{
			super("nameplates", ModuleNameplates.this, "nameplate");

			this.addEnforcer(Enforcers.IS_PLAYER, "nameplate.must_be_player");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();

			Map<Integer, Entry<String, Nameplate>> availableNameplates = Maps.newHashMap();

			int slot = 0;

			for (Entry<String, Nameplate> entry : ModuleNameplates.this.nameplates.entrySet())
			{
				if (!player.hasPermission("nameplates." + entry.getKey())) continue;

				availableNameplates.put(slot++, entry);
			}

			if (availableNameplates.isEmpty())
			{
				player.sendLocalizedMessage("nameplate.none_available");

				return;
			}

			StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(availableNameplates.size(), 9), player.getLocalizedMessage("nameplate.panel"));

			slot = 0;

			for (Entry<Integer, Entry<String, Nameplate>> nameplate : availableNameplates.entrySet())
			{
				Entry<String, Nameplate> plateWrapper = nameplate.getValue();

				panel.addButton(Button.builder().addSlot(slot++, plateWrapper.getValue().getItem()).addAction(event ->
				{
					Entry<String, Nameplate> entry = availableNameplates.get(event.getSlotObject());

					if (entry == null) return;

					Player clicker = event.getPlayer();

					NameplateKey key = ModuleNameplates.this.playerNameplates.get(clicker.getUniqueId());

					if (key == null)
					{
						key = new NameplateKey(entry.getKey(), true);
					}
					else
					{
						key.setKey(entry.getKey());
					}

					clicker.closeInventory();

					ModuleNameplates.this.consumer.accept(clicker, entry.getValue());
				}).build());
			}

			panel.open(player, false);
		}
	}

}