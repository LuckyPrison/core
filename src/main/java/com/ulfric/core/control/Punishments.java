package com.ulfric.core.control;

import java.net.InetAddress;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.coffee.collection.MapUtils;
import com.ulfric.lib.coffee.locale.Locale;

public final class Punishments {

	private static final Punishments INSTANCE = new Punishments();

	public static Punishments getInstance()
	{
		return Punishments.INSTANCE;
	}

	public static Punishment newMute(PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced, boolean silent)
	{
		return Punishments.newMute(holder, punisher, reason, Instant.now(), expiry, referenced, silent);
	}

	public static Punishment newMute(PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced, boolean silent)
	{
		Validate.notNull(holder);
		Validate.notNull(punisher);

		int id = Punishments.getInstance().getAndIncrementCounter();

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("mute.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		Punishment punishment;

		if (silent)
		{
			punishment = new SilentMute(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
		}
		else
		{
			punishment = new Mute(id, holder, punisher, realReason, realCreation, realExpiry, null, null, realReferenced);
		}

		punishment.write();

		return punishment;
	}

	public static Punishment newCommandMute(PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced, boolean silent)
	{
		return Punishments.newCommandMute(holder, punisher, reason, Instant.now(), expiry, referenced, silent);
	}

	public static Punishment newCommandMute(PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced, boolean silent)
	{
		Validate.notNull(holder);
		Validate.notNull(punisher);

		int id = Punishments.getInstance().getAndIncrementCounter();

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("command_mute.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		Punishment punishment;

		if (silent)
		{
			punishment = new SilentCmdMute(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
		}
		else
		{
			punishment = new CmdMute(id, holder, punisher, realReason, realCreation, realExpiry, null, null, realReferenced);
		}

		punishment.write();

		return punishment;
	}

	public static Punishment newShadowMute(PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced)
	{
		return Punishments.newShadowMute(holder, punisher, reason, Instant.now(), expiry, referenced);
	}

	public static Punishment newShadowMute(PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced)
	{
		Validate.notNull(holder);
		Validate.notNull(punisher);

		int id = Punishments.getInstance().getAndIncrementCounter();

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("shadowmute.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		Punishment punishment = new ShadowMute(id, holder, punisher, realReason, realCreation, realExpiry, null, null, realReferenced);
		punishment.write();
		return punishment;
	}

	public static Punishment newBan(PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced, boolean silent)
	{
		return Punishments.newBan(holder, punisher, reason, Instant.now(), expiry, referenced, silent);
	}

	public static Punishment newBan(PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced, boolean silent)
	{
		Validate.notNull(holder);
		Validate.notNull(punisher);

		int id = Punishments.getInstance().getAndIncrementCounter();

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("ban.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		Punishment punishment;

		if (silent)
		{
			punishment = new SilentBan(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
		}
		else
		{
			punishment = new Ban(id, holder, punisher, realReason, realCreation, realExpiry, null, null, realReferenced);
		}

		punishment.write();

		return punishment;
	}

	public static Punishment newWarn(PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced)
	{
		return Punishments.newWarn(holder, punisher, reason, Instant.now(), expiry, referenced);
	}

	public static Punishment newWarn(PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced)
	{
		Validate.notNull(holder);
		Validate.notNull(punisher);

		int id = Punishments.getInstance().getAndIncrementCounter();

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("warn.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		Punishment punishment = new Warn(id, holder, punisher, realReason, realCreation, realExpiry, null, null, realReferenced);
		punishment.write();
		return punishment;
	}

	public static Punishment newKick(PunishmentHolder holder, Punisher punisher, String reason, int[] referenced, boolean silent)
	{
		return Punishments.newKick(holder, punisher, reason, Instant.now(), referenced, silent);
	}

	public static Punishment newKick(PunishmentHolder holder, Punisher punisher, String reason, Instant creation, int[] referenced, boolean silent)
	{
		Validate.notNull(holder);
		Validate.notNull(punisher);

		int id = Punishments.getInstance().getAndIncrementCounter();

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("kick.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		Punishment punishment;

		if (silent)
		{
			punishment = new SilentKick(id, holder, punisher, realReason, realCreation, realReferenced);
		}
		else
		{
			punishment = new Kick(id, holder, punisher, realReason, realCreation, realReferenced);
		}

		punishment.write();

		return punishment;
	}

	public static Punishment newKill(PunishmentHolder holder, Punisher punisher, String reason, int[] referenced, boolean silent)
	{
		return Punishments.newKill(holder, punisher, reason, Instant.now(), referenced, silent);
	}

	public static Punishment newKill(PunishmentHolder holder, Punisher punisher, String reason, Instant creation, int[] referenced, boolean silent)
	{
		Validate.notNull(holder);
		Validate.notNull(punisher);

		int id = Punishments.getInstance().getAndIncrementCounter();

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("kill.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		Punishment punishment;

		if (silent)
		{
			punishment = new SilentKill(id, holder, punisher, realReason, realCreation, realReferenced);
		}
		else
		{
			punishment = new Kill(id, holder, punisher, realReason, realCreation, realReferenced);
		}

		punishment.write();

		return punishment;
	}

	private final AtomicInteger counter = new AtomicInteger();
	private final Map<Object, PunishmentHolder> holders = Maps.newHashMap();
	private final Map<PunishmentType, Map<Integer, Punishment>> punishments = MapUtils.enumMapAllOf(PunishmentType.class, Maps::newHashMap);
	private final Map<Integer, Punishment> allPunishments = Maps.newHashMap();
	private Map<PunishmentType, MapSubscription<Document>> documents;

	private Punishments() { }

	void setDocuments(Map<PunishmentType, MapSubscription<Document>> documents)
	{
		this.documents = documents;
	}

	void write(Punishment punishment)
	{
		MapSubscription<Document> subscription = this.documents.get(punishment.getType());

		MutableDocument document = new SimpleDocument();

		punishment.into(document);

		subscription.setField("p" + punishment.getID(), document);
	}

	public int currentID()
	{
		return this.counter.get();
	}

	int getAndIncrementCounter()
	{
		return this.counter.getAndIncrement();
	}

	int incrementAndGetCounter()
	{
		return this.counter.incrementAndGet();
	}

	public PunishmentHolder getHolder(UUID uuid)
	{
		return this.holders.get(uuid);
	}

	public PunishmentHolder getHolder(InetAddress ip)
	{
		return this.holders.get(ip);
	}

	void registerHolder(PunishmentHolder holder)
	{
		this.holders.put(holder.hasUniqueId() ? holder.getUniqueId() : holder.getIP(), holder);

		for (Punishment punishment : this.allPunishments.values())
		{
			if (punishment.getHolder() != holder) continue;

			holder.addPunishment(punishment);
		}
	}

	public List<Punishment> getAllPunishments(PunishmentType type)
	{
		if (type == null)
		{
			return Lists.newArrayList(this.allPunishments.values());
		}

		return Lists.newArrayList(this.punishments.get(type).values());
	}

	public Punishment getPunishment(int id)
	{
		return this.allPunishments.get(id);
	}

	void registerPunishment(Punishment punishment)
	{
		int id = punishment.getID();
		Integer idInteger = id;

		this.allPunishments.put(idInteger, punishment);
		this.punishments.get(punishment.getType()).put(idInteger, punishment);

		punishment.getHolder().addPunishment(punishment);

		if (id <= this.counter.get()) return;

		this.counter.set(id);
	}

	void dump()
	{
		this.allPunishments.clear();
		this.holders.clear();
		this.punishments.values().forEach(Map::clear);
	}

}
