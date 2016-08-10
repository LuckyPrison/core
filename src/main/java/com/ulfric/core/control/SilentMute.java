package com.ulfric.core.control;

import java.time.Instant;

import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

final class SilentMute extends Mute {

	SilentMute(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, Instant expiry, int[] referenced)
	{
		super(id, holder, punisher, reason, placed, expiry, null, referenced);
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
			if (!player.hasPermission("silentmute.see")) continue;

			StringBuilder builder = new StringBuilder();

			Locale locale = player.getLocale();

			String punished = this.getHolder().getName(player);

			builder.append(locale.getFormattedMessage("silentmute.header", punished, punisher));
			builder.append(locale.getFormattedMessage("silentmute.reason", reason));
			builder.append(expiry == null ? locale.getRawMessage("silentmute.expiry_never") : locale.getFormattedMessage("silentmute.expiry", expiry));

			if (referenced != null)
			{
				builder.append(locale.getFormattedMessage("silentmute.referenced", referenced));
			}

			player.sendMessage(builder.toString());
		}
	}

}