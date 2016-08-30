package com.ulfric.core.rankup;

import org.apache.commons.lang3.StringUtils;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Permissions;
import com.ulfric.lib.coffee.npermission.Track;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.scoreboard.Scoreboard;

public class ModuleRankup extends Module {

	public ModuleRankup()
	{
		super("rankup", "Rankups module", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandRankup(this));
		this.addCommand(new CommandTracks(this));

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Scoreboard board = event.getPlayer().getScoreboard();
				board.addElement(new ElementMine(board));
				board.addElement(new ElementNextMine(board));
			}

			@Handler
			public void onRankup(PlayerRankupEvent event)
			{
				Player player = event.getPlayer();

				Scoreboard board = player.getScoreboard();

				board.elementFromClazz(ElementMine.class).update(player);
				board.elementFromClazz(ElementNextMine.class).update(player);
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = this.getModuleConfig();

		Document document = config.getRoot();

		Rankups.INSTANCE.defaultTrack = Permissions.getTrack(document.getString("default-track", "mines"));

		Document tracksDoc = document.getDocument("tracks");

		if (tracksDoc != null)
		{
			for (String key : tracksDoc.getKeys(false))
			{
				Document trackDoc = tracksDoc.getDocument(key);

				if (trackDoc == null) continue;

				Track track = Permissions.getTrack(trackDoc.getString("track", key));

				if (track == null) continue;

				MaterialData data = MaterialData.of(trackDoc.getString("material", "dirt"));

				if (data == null) continue;

				Rankups.INSTANCE.trackItems.put(track, data);
			}
		}

		Document ranksDoc = document.getDocument("rankups");

		if (ranksDoc != null)
		{
			for (String key : ranksDoc.getKeys(false))
			{
				Document rankDoc = ranksDoc.getDocument(key);

				if (rankDoc == null) continue;

				Group group = Permissions.getGroup(rankDoc.getString("group", key));

				if (group == null) continue;

				String cost = rankDoc.getString("price");
				CurrencyAmount price = StringUtils.isBlank(cost) ? null : CurrencyAmount.valueOf(cost);

				Rankups.INSTANCE.registerRankup(group, price);
			}
		}
	}

	@Override
	public void onModuleDisable()
	{
		Rankups.INSTANCE.clear();
	}

}