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
import com.ulfric.lib.coffee.collection.MapUtils;
import com.ulfric.lib.coffee.locale.Locale;

public final class Punishments {

	private static final Punishments INSTANCE = new Punishments();

	public static Punishments getInstance()
	{
		return Punishments.INSTANCE;
	}

	public static Punishment newMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced, boolean silent)
	{
		Validate.isTrue(id >= 0);
		Validate.notNull(holder);

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("mute.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		if (silent)
		{
			return new SilentMute(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
		}

		return new Mute(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
	}

	public static Punishment newCommandMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced, boolean silent)
	{
		return Punishments.newCommandMute(id, holder, punisher, reason, Instant.now(), expiry, referenced, silent);
	}

	public static Punishment newCommandMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced, boolean silent)
	{
		Validate.isTrue(id >= 0);
		Validate.notNull(holder);

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("command_mute.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		if (silent)
		{
			return new SilentCmdMute(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
		}

		return new CmdMute(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
	}

	public static Punishment newMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced, boolean silent)
	{
		return Punishments.newMute(id, holder, punisher, reason, Instant.now(), expiry, referenced, silent);
	}

	public static Punishment newShadowMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced)
	{
		return Punishments.newShadowMute(id, holder, punisher, reason, Instant.now(), expiry, referenced);
	}

	public static Punishment newShadowMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced)
	{
		Validate.isTrue(id >= 0);
		Validate.notNull(holder);

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("shadowmute.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		return new ShadowMute(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
	}

	public static Punishment newBan(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced, boolean silent)
	{
		Validate.isTrue(id >= 0);
		Validate.notNull(holder);

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("ban.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		if (silent)
		{
			return new SilentBan(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
		}

		return new Ban(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
	}

	public static Punishment newWarn(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced)
	{
		return Punishments.newWarn(id, holder, punisher, reason, Instant.now(), expiry, referenced);
	}

	public static Punishment newWarn(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant creation, Instant expiry, int[] referenced)
	{
		Validate.isTrue(id >= 0);
		Validate.notNull(holder);

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("warn.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		Instant realExpiry = expiry == null ? Instant.MAX : expiry;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		return new Warn(id, holder, punisher, realReason, realCreation, realExpiry, realReferenced);
	}

	public static Punishment newBan(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant expiry, int[] referenced, boolean silent)
	{
		return Punishments.newBan(id, holder, punisher, reason, Instant.now(), expiry, referenced, silent);
	}

	public static Punishment newKick(int id, PunishmentHolder holder, Punisher punisher, String reason, int[] referenced, boolean silent)
	{
		return Punishments.newKick(id, holder, punisher, reason, Instant.now(), referenced, silent);
	}

	public static Punishment newKick(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant creation, int[] referenced, boolean silent)
	{
		Validate.isTrue(id >= 0);
		Validate.notNull(holder);

		String realReason = StringUtils.isBlank(reason) ? Locale.getDefault().getRawMessage("kick.default_reason") : reason.trim();
		Instant realCreation = creation == null ? Instant.now() : creation;
		int[] realReferenced = referenced == null ? new int[0] : referenced;

		if (silent)
		{
			return new SilentKick(id, holder, punisher, realReason, realCreation, realReferenced);
		}

		return new Kick(id, holder, punisher, realReason, realCreation, realReferenced);
	}

	private final AtomicInteger counter = new AtomicInteger();
	private final Map<Object, PunishmentHolder> holders = Maps.newHashMap();
	private final Map<PunishmentType, Map<Integer, Punishment>> punishments = MapUtils.enumMapAllOf(PunishmentType.class, Maps::newHashMap);

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
	}

	public Punisher getPunisher(UUID uuid)
	{
		return this.holders.get(uuid);
	}

	public List<Punishment> getAllPunishments(PunishmentType type)
	{
		return Lists.newArrayList(this.punishments.get(type).values());
	}

	public Punishment getPunishment(int id)
	{
		Integer idInteger = id;

		for (Map<Integer, Punishment> map : this.punishments.values())
		{
			Punishment punishment = map.get(idInteger);

			if (punishment == null) continue;

			return punishment;
		}

		return null;
	}

	void registerPunishment(Punishment punishment)
	{
		int id = punishment.getID();
		Integer idInteger = id;

		this.punishments.get(null).put(idInteger, punishment);
		this.punishments.get(punishment.getType()).put(idInteger, punishment);

		if (id <= this.counter.get()) return;

		this.counter.set(id);
	}

	void dump()
	{
		this.holders.clear();

		this.punishments.values().forEach(Map::clear);
	}

}