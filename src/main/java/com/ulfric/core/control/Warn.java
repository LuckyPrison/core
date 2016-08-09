package com.ulfric.core.control;

import java.time.Instant;
import java.util.List;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

class Warn extends TimedPunishment {

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

		return Punishments.newWarn(id, holder, punisher, reason, creation, expiry, referenced);
	}

	Warn(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, Instant expiry, int[] referenced)
	{
		super(id, PunishmentType.WARN, holder, punisher, reason, placed, expiry, referenced);
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

			builder.append(locale.getFormattedMessage("warn.header", punished, punisher));
			builder.append(locale.getFormattedMessage("warn.reason", reason));
			builder.append(expiry == null ? locale.getRawMessage("warn.expiry_never") : locale.getFormattedMessage("warn.expiry", expiry));

			if (referenced != null)
			{
				builder.append(locale.getFormattedMessage("warn.referenced", referenced));
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
			PlayerUtils.getOnlinePlayers(holder.getIP()).forEach(this::warn);
		}

		if (!holder.hasUniqueId()) return;

		Player player = PlayerUtils.getOnlinePlayer(holder.getUniqueId());

		if (player == null) return;

		this.warn(player);
	}

	private void warn(Player player)
	{
		player.title().send(player.getLocalizedMessage("warned.title"), null, 9, 200, 11);
	}

}