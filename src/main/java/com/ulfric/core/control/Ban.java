package com.ulfric.core.control;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

class Ban extends TimedPunishment {

	public static Punishment fromDocument(Document document)
	{
		int id = document.getInteger("id");
		PunishmentHolder holder = PunishmentHolder.valueOf(document.getString("holder"));
		Punisher punisher = PunishmentHolder.valueOf(document.getString("punisher"));
		String reason = document.getString("reason");
		Instant creation = Instant.ofEpochMilli(document.getLong("creation"));

		long expiryValue = document.getLong("expiry");
		Instant expiry = expiryValue == -1 ? Instant.MAX : Instant.ofEpochMilli(expiryValue);

		List<Integer> referencedList = document.getIntegerList("referenced");
		int size = referencedList.size();
		int[] referenced = new int[size];
		for (int x = 0; x < size; x++)
		{
			referenced[x] = referencedList.get(x);
		}

		Punisher updater = null;
		String updaterStr = document.getString("updater");

		if (!StringUtils.isBlank(updaterStr))
		{
			updater = Punisher.valueOf(updaterStr);
		}

		String updateReason = document.getString("update-reason");

		return new Ban(id, holder, punisher, reason, creation, expiry, updater, updateReason, referenced);
	}

	Ban(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, Instant expiry, Punisher updater, String updateReason, int[] referenced)
	{
		super(id, PunishmentType.BAN, holder, punisher, reason, placed, expiry, updater, updateReason, referenced);
	}

	@Override
	public void broadcast()
	{
		String punisher = this.getPunisher().getName();
		String reason = this.getReason();
		String expiry = this.hasExpiry() ? this.expiryToString() : null;
		String referenced = this.getReferencedString();

		for (Player player : PlayerUtils.getOnlinePlayers())
		{
			StringBuilder builder = new StringBuilder();

			Locale locale = player.getLocale();

			String punished = this.getHolder().getName(player);

			builder.append(locale.getFormattedMessage("ban.header", punished, punisher));
			builder.append(locale.getFormattedMessage("ban.reason", reason));
			builder.append(expiry == null ? locale.getRawMessage("ban.expiry_never") : locale.getFormattedMessage("ban.expiry", expiry));

			if (referenced != null)
			{
				builder.append(locale.getFormattedMessage("ban.referenced", referenced));
			}

			player.sendMessage(builder.toString());
		}
	}

	@Override
	public void execute()
	{
		PunishmentHolder holder = this.getHolder();

		if (holder.hasIP())
		{
			PlayerUtils.getOnlinePlayers(holder.getIP()).forEach(this::kickBan);
		}

		if (!holder.hasUniqueId()) return;

		Player player = PlayerUtils.getOnlinePlayer(holder.getUniqueId());

		if (player == null) return;

		this.kickBan(player);
	}

	public final String getKickReason(Player player)
	{
		StringBuilder builder = new StringBuilder();

		Locale locale = player == null ? Locale.getDefault() : player.getLocale();

		builder.append(locale.getRawMessage("banned.header"));
		builder.append('\n');

		builder.append(locale.getFormattedMessage("banned.reason", this.getReason()));
		builder.append('\n');

		if (this.hasExpiry())
		{
			builder.append(locale.getFormattedMessage("banned.expiry", this.expiryToString()));
			builder.append('\n');
		}

		Punisher punisher = this.getPunisher();

		builder.append(locale.getFormattedMessage("banned.punisher", player == null ? punisher.getName() : punisher.getName(player)));

		if (this.getExpiry().minusSeconds(10).isBefore(Instant.now()))
		{
			builder.append('\n');
			builder.append(locale.getRawMessage("banned.rejoin"));
		}

		return builder.toString();
	}

	private void kickBan(Player player)
	{
		player.connection().kick(this.getKickReason(player));
	}

}