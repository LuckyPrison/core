package com.ulfric.core.control;

import java.time.Instant;
import java.util.List;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

class Mute extends TimedPunishment {

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

		return Punishments.newMute(id, holder, punisher, reason, creation, expiry, referenced, false);
	}

	Mute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, Instant expiry, int[] referenced)
	{
		super(id, PunishmentType.MUTE, holder, punisher, reason, placed, expiry, referenced);
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

			builder.append(locale.getFormattedMessage("mute.header", punished, punisher));
			builder.append(locale.getFormattedMessage("mute.reason", reason));
			builder.append(expiry == null ? locale.getRawMessage("mute.expiry_never") : locale.getFormattedMessage("mute.expiry", expiry));

			if (referenced != null)
			{
				builder.append(locale.getFormattedMessage("mute.referenced", referenced));
			}

			player.sendMessage(builder.toString());
		}
	}

	public final String getReason(Player player)
	{
		StringBuilder builder = new StringBuilder();

		Locale locale = player == null ? Locale.getDefault() : player.getLocale();

		builder.append(locale.getRawMessage("muted.header"));
		builder.append('\n');

		builder.append(locale.getFormattedMessage("muted.reason", this.getReason()));
		builder.append('\n');

		if (this.hasExpiry())
		{
			builder.append(locale.getFormattedMessage("muted.expiry", this.expiryToString()));
			builder.append('\n');
		}

		Punisher punisher = this.getPunisher();
		builder.append(locale.getFormattedMessage("muted.punisher", player == null ? punisher.getName() : punisher.getName(player)));

		return builder.toString();
	}

}