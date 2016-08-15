package com.ulfric.core.gangs;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.AsyncPlayerChatEvent;
import com.ulfric.lib.craft.string.ChatUtils;

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

		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onChat(AsyncPlayerChatEvent event)
			{
				Player player = event.getPlayer();

				ChatChannel channel = player.getMetadataAs("gang_channel", ChatChannel.class);

				if (channel == null || channel == ChatChannel.GLOBAL) return;

				Gangs gangs = Gangs.getInstance();
				UUID uuid = player.getUniqueId();
				GangMember member = gangs.getMember(uuid);

				if (member == null) return;

				Gang gang = member.getGang();

				String message = event.getMessage();

				String stars = member.getRank().getStars();

				String name = player.getName();

				if (channel == ChatChannel.GANG)
				{
					String format = ChatUtils.color(Strings.format("&7{0} &a{1}&f: &a{2}", stars, name, message));

					for (Player allPlayers : gang.getOnlinePlayers())
					{
						allPlayers.sendMessage(format);
					}

					return;
				}

				if (channel == ChatChannel.ALLY)
				{
					String formatLocal = ChatUtils.color(Strings.format("&7{0} &d{1}&f: &d{2}", stars, name, message));

					for (Player allPlayers : gang.getOnlinePlayers())
					{
						allPlayers.sendMessage(formatLocal);
					}

					List<UUID> allies = gang.getRelations(Relation.ALLY);

					if (allies.isEmpty()) return;

					String gangName = gang.getName();

					String allyFormat = ChatUtils.color(Strings.format("&7{0}{1} &d{2}&f: &d{3}", stars, gangName, name, message));

					for (UUID allyUUID : allies)
					{
						Gang ally = gangs.getGang(allyUUID);

						if (ally == null) continue;

						for (Player allPlayers : gang.getOnlinePlayers())
						{
							allPlayers.sendMessage(allyFormat);
						}
					}

					return;
				}

				throw new UnsupportedOperationException("Unknown channel: " + channel);
			}
		});
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