package com.ulfric.core.control;

import java.time.Instant;

import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

final class SilentKick extends Kick {

	SilentKick(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, int[] referenced)
	{
		super(id, holder, punisher, reason, placed, referenced);
	}

	@Override
	public void broadcast()
	{
		String punisher = this.getPunisher().getName();
		String reason = this.getReason();
		String referenced = this.getReferencedString();

		for (Player player : PlayerUtils.getOnlinePlayers())
		{
			if (!player.hasPermission("silentkick.see")) continue;

			StringBuilder builder = new StringBuilder();

			Locale locale = player.getLocale();

			String punished = this.getHolder().getName(player);

			builder.append(locale.getFormattedMessage("silentkick.header", punished, punisher));
			builder.append(locale.getFormattedMessage("silentkick.reason", reason));

			if (referenced != null)
			{
				builder.append(locale.getFormattedMessage("silentkick.referenced", referenced));
			}

			player.sendMessage(builder.toString());
		}
	}

}