package com.ulfric.core.control;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DataSubscription;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.AsyncPlayerChatEvent;
import com.ulfric.lib.craft.event.player.AsyncPreLoginEvent;
import com.ulfric.lib.craft.event.player.PlayerCommandPreProcessEvent;

public final class ModulePunishments extends Module {

	public ModulePunishments()
	{
		super("punishments", "Punishment manager module", "1.0.0", "Packet");
	}

	private Map<PunishmentType, MapSubscription<Document>> documents;
	private Set<String> allowedCommandMutes;

	@Override
	public void onFirstEnable()
	{
		this.documents = Maps.newEnumMap(PunishmentType.class);

		Punishments.getInstance().setDocuments(this.documents);

		DataManager manager = DataManager.get();

		DocumentStore store = manager.getEnsuredDatabase("punishments");

		for (PunishmentType type : PunishmentType.values())
		{
			String name = type.name().replace("_", "").toLowerCase();

			manager.ensureTableCreated(store, name);

			this.documents.put(type, store.document(new DataAddress<>(name, "punishments", null)).blockOnSubscribe(true).subscribe());
		}

		this.allowedCommandMutes = Sets.newHashSet();

		//this.addModule(new ModuleNotes());

		this.addCommand(new CommandKick(this));
		this.addCommand(new CommandBan(this));
		this.addCommand(new CommandMute(this));
		this.addCommand(new CommandCommandMute(this));
		this.addCommand(new CommandShadowMute(this));
		this.addCommand(new CommandKill(this));
		this.addCommand(new CommandWarn(this));
		this.addCommand(new CommandWarns(this));
		this.addCommand(new CommandLift(this));
		this.addCommand(new CommandPardon(this));
		this.addCommand(new CommandUnmute(this));
		this.addCommand(new CommandUncommandmute(this));

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(AsyncPreLoginEvent event)
			{
				if (event.getResult() != AsyncPreLoginEvent.Result.ALLOWED) return;

				Punishments cache = Punishments.getInstance();

				PunishmentHolder holder = cache.getHolder(event.getUniqueId());

				if (ModulePunishments.this.kickBan(event, holder)) return;

				holder = cache.getHolder(event.getIpAddress());

				ModulePunishments.this.kickBan(event, holder);
			}

			@Handler(ignoreCancelled = true)
			public void onChat(AsyncPlayerChatEvent event)
			{
				Player player = event.getPlayer();

				Punishments cache = Punishments.getInstance();

				PunishmentHolder holder = cache.getHolder(player.getUniqueId());

				if (ModulePunishments.this.noChat(event, holder)) return;

				holder = cache.getHolder(player.getIP());

				ModulePunishments.this.noChat(event, holder);
			}

			@Handler(ignoreCancelled = true)
			public void onChatShadow(AsyncPlayerChatEvent event)
			{
				Player player = event.getPlayer();

				Punishments cache = Punishments.getInstance();

				PunishmentHolder holder = cache.getHolder(player.getIP());

				if (ModulePunishments.this.shadowChat(event, holder)) return;

				holder = cache.getHolder(player.getUniqueId());

				ModulePunishments.this.shadowChat(event, holder);
			}

			@Handler(ignoreCancelled = true)
			public void onCommand(PlayerCommandPreProcessEvent event)
			{
				Player player = event.getPlayer();

				Punishments cache = Punishments.getInstance();

				PunishmentHolder holder = cache.getHolder(player.getUniqueId());

				if (ModulePunishments.this.noCommand(event, holder)) return;

				holder = cache.getHolder(player.getIP());

				ModulePunishments.this.noCommand(event, holder);
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		this.documents.values().forEach(DataSubscription::subscribe);

		Punishments punishments = Punishments.getInstance();

		for (Map.Entry<PunishmentType, MapSubscription<Document>> entry : this.documents.entrySet())
		{
			PunishmentType type = entry.getKey();
			MapSubscription<Document> subscription = entry.getValue();

			Document document = subscription.getValue();

			if (document == null) continue;

			Set<String> keys = document.getKeys(false);

			if (SetUtils.isEmpty(keys)) continue;

			for (String key : keys)
			{
				Document punishmentDoc = document.getDocument(key);

				Punishment punishment = type.fromDocument(punishmentDoc);

				if (punishment == null) continue;

				punishments.registerPunishment(punishment);
			}
		}

		Document document = this.getModuleConfig().getRoot();

		this.allowedCommandMutes.addAll(document.getStringList("command-mute-allowed-commands", ImmutableList.of()));
	}

	@Override
	public void onModuleDisable()
	{
		this.allowedCommandMutes.clear();

		Punishments punishments = Punishments.getInstance();

		punishments.dump();

		this.documents.values().forEach(DataSubscription::unsubscribe);
	}

	boolean kickBan(AsyncPreLoginEvent event, PunishmentHolder holder)
	{
		if (holder == null) return false;

		List<Punishment> bans = holder.getPunishments(PunishmentType.BAN);

		if (ListUtils.isEmpty(bans)) return false;

		for (Punishment punishment : bans)
		{
			if (!(punishment instanceof Ban)) continue;

			Ban ban = (Ban) punishment;

			if (ban.isExpired()) continue;

			event.setKickMessage(ban.getKickReason(null));

			event.setResult(AsyncPreLoginEvent.Result.KICK_BANNED);

			return true;
		}

		return false;
	}

	boolean noChat(AsyncPlayerChatEvent event, PunishmentHolder holder)
	{
		if (holder == null) return false;

		List<Punishment> mutes = holder.getPunishments(PunishmentType.MUTE);

		if (ListUtils.isEmpty(mutes)) return false;

		for (Punishment punishment : mutes)
		{
			if (!(punishment instanceof Mute)) continue;

			Mute mute = (Mute) punishment;

			if (mute.isExpired()) continue;

			event.setCancelled(true);

			Player player = event.getPlayer();

			player.sendMessage(mute.getReason(player));

			return true;
		}

		return false;
	}

	boolean noCommand(PlayerCommandPreProcessEvent event, PunishmentHolder holder)
	{
		if (holder == null) return false;

		List<Punishment> cmutes = holder.getPunishments(PunishmentType.COMMAND_MUTE);

		if (ListUtils.isEmpty(cmutes)) return false;

		for (Punishment punishment : cmutes)
		{
			if (!(punishment instanceof CmdMute)) continue;

			CmdMute cmute = (CmdMute) punishment;

			if (cmute.isExpired()) continue;

			event.setCancelled(true);

			Player player = event.getPlayer();

			player.sendMessage(cmute.getReason(player));

			return true;
		}

		return false;
	}

	boolean shadowChat(AsyncPlayerChatEvent event, PunishmentHolder holder)
	{
		if (holder == null) return false;

		List<Punishment> bans = holder.getPunishments(PunishmentType.SHADOW_MUTE);

		if (ListUtils.isEmpty(bans)) return false;

		for (Punishment punishment : bans)
		{
			if (!(punishment instanceof ShadowMute)) continue;

			ShadowMute mute = (ShadowMute) punishment;

			if (mute.isExpired()) continue;

			Set<Player> recipients = event.getRecipients();

			recipients.clear();

			recipients.addAll(holder.toPlayers());

			return true;
		}

		return false;
	}

}
