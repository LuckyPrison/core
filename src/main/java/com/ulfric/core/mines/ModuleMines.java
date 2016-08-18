package com.ulfric.core.mines;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.region.Region;
import com.ulfric.lib.coffee.region.RegionList;
import com.ulfric.lib.coffee.string.StringUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.region.RegionColl;

public class ModuleMines extends Module {

	public ModuleMines()
	{
		super("mines", "Mine resetting & management", "1.0.0", "Packet");
	}

	Set<Mine> resetQueue;

	@Override
	public void onModuleDisable()
	{
		this.resetQueue.clear();

		Mines.INSTANCE.clear();
	}

	@Override
	public void onModuleEnable()
	{
		Document mines = this.getModuleConfig().getRoot().getDocument("mines");

		for (String key : mines.getKeys(false))
		{
			Document mineDocument = mines.getDocument(key);

			if (mineDocument == null) continue;

			Mine mine = Mine.fromDocument(mineDocument);

			if (mine == null) continue;

			Mines.INSTANCE.registerMine(mine);

			this.resetQueue.add(mine);
		}
	}

	@Override
	public void onFirstEnable()
	{
		this.resetQueue = Sets.newHashSet();

		this.addCommand(new CommandMineReset(this));

		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onBreak(BlockBreakEvent event)
			{
				RegionList list = RegionColl.at(event.getBlock().getLocation());

				for (Region region : list)
				{
					Mine mine = Mines.INSTANCE.getByRegion(region);

					if (mine == null) continue;

					ModuleMines.this.resetQueue.add(mine);

					mine.increaseCounter();

					break;
				}
			}
		});

		ThreadUtils.runRepeating(() ->
		{
			if (this.resetQueue.isEmpty()) return;

			Set<Mine> set = Sets.newTreeSet(this.resetQueue);
			int size = Math.min(set.size(), 3);

			Iterator<Mine> iterator = set.iterator();

			List<Mine> reset = Lists.newArrayListWithCapacity(size);

			for (int x = 0; x < size; x++)
			{
				if (!iterator.hasNext()) break;

				Mine next = iterator.next();

				if (!next.reset())
				{
					x--;

					continue;
				}

				reset.add(next);

				this.resetQueue.remove(next);
			}

			if (reset.isEmpty()) return;

			String resetString = StringUtils.mergeNicely(reset);

			for (Player player : PlayerUtils.getOnlinePlayers())
			{
				player.sendLocalizedMessage("mines.reset", resetString);
			}
		}, 30 * 20);
	}

}