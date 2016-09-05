package com.ulfric.core.rankup;

import java.util.Map;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.economy.BalanceChangeEvent;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Permissions;
import com.ulfric.lib.coffee.npermission.Track;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.scoreboard.Scoreboard;

public final class ModuleRankup extends Module {

	public static final ModuleRankup INSTANCE = new ModuleRankup();

	private ModuleRankup()
	{
		super("rankup", "Module for handling rankups", "1.0.0", "Packet");
	}

	private Map<Track, Map<Group, CurrencyAmount>> prices;

	public Rankup getNextRank(Player player)
	{
		if (this.prices == null) return null;

		Group next = player.getNextGroup();

		if (next == null) return null;

		Track track = player.getCurrentTrack();

		if (track == null) return null;

		Map<Group, CurrencyAmount> amts = this.prices.get(track);

		if (amts == null) return null;

		Group current = player.getCurrentGroup();

		return new Rankup(current, next, amts.get(next));
	}

	@Override
	public void onFirstEnable()
	{
		this.prices = Maps.newHashMap();

		this.addCommand(new CommandRankup(this));

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
			public void onBalanceChange(BalanceChangeEvent event)
			{
				Player player = PlayerUtils.getPlayer(event.getAccount().getUniqueId());

				if (player == null) return;

				Scoreboard board = player.getScoreboard();

				board.elementFromClazz(ElementNextMine.class).update(player);
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
		Document doc = this.getModuleConfig().getRoot().getDocument("rankups");

		if (doc == null) return;

		int total = 0;

		for (String key : doc.getKeys(false))
		{
			Document rankupDoc = doc.getDocument(key);

			if (rankupDoc == null) continue;

			Track track = Permissions.getTrack(rankupDoc.getString("track", key));

			if (track == null) continue;

			Map<Group, CurrencyAmount> rankups = this.prices.computeIfAbsent(track, k -> Maps.newTreeMap());

			Document ranks = rankupDoc.getDocument("ranks");

			if (ranks == null) continue;

			for (String rankKey : ranks.getKeys(false))
			{
				Document rankDoc = ranks.getDocument(rankKey);

				if (rankDoc == null) continue;

				Group rank = Permissions.getGroup(rankDoc.getString("group", rankKey));

				if (rank == null) continue;

				if (!track.hasGroup(rank)) continue;

				String priceStr = rankDoc.getString("price");
				CurrencyAmount amt = priceStr == null ? null : CurrencyAmount.valueOf(priceStr);

				if (rankups.put(rank, amt) != null) continue;

				total++;
			}
		}

		this.log("Registered " + total + " rankups");
	}

	@Override
	public void onModuleDisable()
	{
		this.prices.clear();
	}

}