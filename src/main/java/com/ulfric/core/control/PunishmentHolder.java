package com.ulfric.core.control;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public final class PunishmentHolder extends Punisher implements Noteable {

	public static final Argument ARGUMENT = Argument.builder().setPath("holder").addResolver((sen, str) -> PunishmentHolder.valueOf(str)).setUsage("control.specify_holder").build();

	public static PunishmentHolder valueOf(String contextual)
	{
		Punishments cache = Punishments.getInstance();

		try
		{
			InetAddress address = InetAddress.getByName(contextual);

			Validate.notNull(address);

			PunishmentHolder holder = cache.getHolder(address);

			if (holder != null) return holder;

			holder = new PunishmentHolder(address);

			cache.registerHolder(holder);

			return holder;
		}
		catch (UnknownHostException exception2)
		{
			OfflinePlayer player = PlayerUtils.getOfflinePlayer(contextual);

			if (player == null) return null;

			UUID uuid = player.getUniqueId();

			PunishmentHolder holder = cache.getHolder(uuid);

			if (holder != null) return holder;

			String name = player.getName();

			holder = new PunishmentHolder(uuid, name == null ? "*UNKNOWN*" : name);

			cache.registerHolder(holder);

			return holder;
		}
	}

	public static PunishmentHolder valueOf(UUID uuid, String name)
	{
		Punishments cache = Punishments.getInstance();

		PunishmentHolder holder = cache.getHolder(uuid);

		if (holder != null) return holder;

		holder = new PunishmentHolder(uuid, name);

		cache.registerHolder(holder);

		return holder;
	}

	private PunishmentHolder(UUID uuid, String name)
	{
		super(name, uuid);
		this.address = null;
	}

	private PunishmentHolder(InetAddress ip)
	{
		super(ip.getHostAddress(), null);
		this.address = ip;
	}

	private final InetAddress address;
	private Map<PunishmentType, Set<Punishment>> punishments;

	public InetAddress getIP()
	{
		return this.address;
	}

	public boolean hasIP()
	{
		return this.address != null;
	}

	@Override
	public String getName(CommandSender sender)
	{
		if (!this.hasIP()) return this.getName();

		if (sender.hasPermission("control.viewips")) return this.getName();

		return sender.getLocalizedMessage("control.ip_hidden");
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof PunishmentHolder)) return false;

		PunishmentHolder holder = (PunishmentHolder) object;

		if (this.hasUniqueId())
		{
			if (!holder.hasUniqueId()) return false;

			return this.getUniqueId().equals(holder.getUniqueId());
		}

		if (this.hasIP())
		{
			if (!holder.hasIP()) return false;

			return this.getIP().equals(holder.getIP());
		}

		throw new IllegalStateException();
	}

	@Override
	public int hashCode()
	{
		if (this.hasUniqueId())
		{
			return this.getUniqueId().hashCode();
		}

		return this.getIP().hashCode();
	}

	@Override
	public String toString()
	{
		return this.hasUniqueId() ? this.getUniqueId().toString() : this.getIP().toString();
	}

	public boolean matches(Player player)
	{
		if (this.hasUniqueId())
		{
			return this.getUniqueId().equals(player.getUniqueId());
		}

		return this.getIP().equals(player.connection().getIP());
	}

	public List<Punishment> getPunishments(PunishmentType type)
	{
		Validate.notNull(type);

		if (this.punishments == null) return null;

		Set<Punishment> foundPunishments = this.punishments.get(type);

		if (SetUtils.isEmpty(foundPunishments)) return null;

		return Lists.newArrayList(foundPunishments);
	}

	public boolean addPunishment(Punishment punishment)
	{
		Validate.notNull(punishment);

		if (this.punishments == null)
		{
			this.punishments = Maps.newEnumMap(PunishmentType.class);
		}

		PunishmentType type = punishment.getType();

		Set<Punishment> foundPunishments = this.punishments.get(type);

		if (foundPunishments == null)
		{
			foundPunishments = Sets.newTreeSet();

			this.punishments.put(type, foundPunishments);
		}

		return foundPunishments.add(punishment);
	}

	public void sendMessage(String message)
	{
		this.sendMessage(player -> player.sendMessage(message));
	}

	public void sendLocalizedMessage(String message)
	{
		this.sendMessage(player -> player.sendLocalizedMessage(message));
	}

	public void sendLocalizedMessage(String message, Object... objects)
	{
		this.sendMessage(player -> player.sendLocalizedMessage(message, objects));
	}

	private void sendMessage(Consumer<Player> consumer)
	{
		if (this.hasUniqueId())
		{
			Player player = PlayerUtils.getOnlinePlayer(this.getUniqueId());

			if (player != null)
			{
				consumer.accept(player);
			}
		}

		if (!this.hasIP()) return;

		PlayerUtils.getOnlinePlayers(this.getIP()).forEach(consumer::accept);
	}

	public Collection<Player> toPlayers()
	{
		Collection<Player> players = this.hasIP() ? PlayerUtils.getOnlinePlayers(this.getIP()) : Lists.newArrayList();

		if (!this.hasUniqueId()) return players;

		Player player = PlayerUtils.getOnlinePlayer(this.getUniqueId());

		if (player == null) return players;

		players.add(player);

		return players;
	}

	@Override
	public NoteType getNoteType()
	{
		return NoteType.PUNISHMENT_HOLDER;
	}

}