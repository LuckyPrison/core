package com.ulfric.core.mines;

import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.tuple.Weighted;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.world.World;
import com.ulfric.lib.data.document.Document;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class Mines {

	private Set<Mine> mines;
	private World mineWorld;

	public Mines(Document mineDoc, World mineWorld, long resetInterval)
	{
		this.mines = Sets.newHashSetWithExpectedSize(mineDoc.getKeys(false).size());
		this.mineWorld = mineWorld;
		ThreadUtils.runRepeating(new MineResetTask(), resetInterval * 20);
	}

	class MineResetTask implements Runnable {

		@Override
		public void run()
		{
			for (Mine mine : mines)
			{
				int totalWeight = mine.getContents().stream().mapToInt(Weighted::getWeight).sum();
				MultiBlockChange operation = new MultiBlockChange();
				mine.getRegion().getCuboid().forEach(v -> operation.addBlock(v, RandomUtils.randomValue(mine.getContents(), totalWeight)));
				ThreadUtils.runAsync(operation);
				PlayerUtils.getOnlinePlayers().stream().filter(mine::hasPermission).forEach(p -> p.sendLocalizedMessage("core.mines.reset." + mine.getName()));
			}
		}
	}

	private class MultiBlockChange implements Runnable {
		private Set<Pair<Vector, Material>> blocks;

		private MultiBlockChange()
		{
			this.blocks = new HashSet<>();
		}

		private void addBlock(Vector v, Material m)
		{
			this.blocks.add(ImmutablePair.of(v, m));
		}

		@Override
		public void run()
		{
			blocks.forEach(p -> mineWorld.getBlock(p.getKey()).setType(p.getValue()));
		}
	}
}
