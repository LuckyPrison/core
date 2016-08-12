package com.ulfric.core.control;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

class ShadowMute extends TimedPunishment {

	public static Punishment fromDocument(Document document)
	{
		int id = document.getInteger("id");
		PunishmentHolder holder = PunishmentHolder.valueOf(document.getString("holder"));
		Punisher punisher = Punisher.valueOf(document.getString("punisher"));
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

		return new ShadowMute(id, holder, punisher, reason, creation, expiry, updater, updateReason, referenced);
	}

	ShadowMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, Instant expiry, Punisher updater, String updateReason, int[] referenced)
	{
		super(id, PunishmentType.SHADOW_MUTE, holder, punisher, reason, placed, expiry, updater, updateReason, referenced);
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
			if (!player.hasPermission("shadowmute.see")) continue;

			StringBuilder builder = new StringBuilder();

			Locale locale = player.getLocale();

			String punished = this.getHolder().getName(player);

			builder.append(locale.getFormattedMessage("shadowmute.header", punished, punisher));
			builder.append(locale.getFormattedMessage("shadowmute.reason", reason));
			builder.append(expiry == null ? locale.getRawMessage("shadowmute.expiry_never") : locale.getFormattedMessage("shadowmute.expiry", expiry));

			if (referenced != null)
			{
				builder.append(locale.getFormattedMessage("shadowmute.referenced", referenced));
			}

			player.sendMessage(builder.toString());
		}
	}

}