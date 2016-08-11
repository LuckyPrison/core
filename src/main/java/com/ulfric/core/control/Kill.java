package com.ulfric.core.control;

import java.time.Instant;
import java.util.List;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

class Kill extends Punishment {

	public static Punishment fromDocument(Document document)
	{
		int id = document.getInteger("id");
		PunishmentHolder holder = PunishmentHolder.valueOf(document.getString("holder"));
		Punisher punisher = Punisher.valueOf(document.getString("punisher"));
		String reason = document.getString("reason");
		Instant creation = Instant.ofEpochMilli(document.getLong("creation"));

		List<Integer> referencedList = document.getIntegerList("referenced");
		int size = referencedList.size();
		int[] referenced = new int[size];
		for (int x = 0; x < size; x++)
		{
			referenced[x] = referencedList.get(x);
		}

		return new Kill(id, holder, punisher, reason, creation, referenced);
	}

	Kill(int id, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, int[] referenced)
	{
		super(id, PunishmentType.KILL, holder, punisher, reason, placed, referenced);
	}

	@Override
	public void broadcast()
	{
		String punisher = this.getPunisher().getName();
		String reason = this.getReason();
		String referenced = this.getReferencedString();

		for (Player player : PlayerUtils.getOnlinePlayers())
		{
			StringBuilder builder = new StringBuilder();

			Locale locale = player.getLocale();

			String punished = this.getHolder().getName(player);

			builder.append(locale.getFormattedMessage("kill.header", punished, punisher));
			builder.append(locale.getFormattedMessage("kill.reason", reason));

			if (referenced != null)
			{
				builder.append(locale.getFormattedMessage("kill.referenced", referenced));
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
			PlayerUtils.getOnlinePlayers(holder.getIP()).forEach(this::kill);
		}

		if (!holder.hasUniqueId()) return;

		Player player = PlayerUtils.getOnlinePlayer(holder.getUniqueId());

		if (player == null) return;

		this.kill(player);
	}

	private void kill(Player player)
	{
		player.kill();
	}

}