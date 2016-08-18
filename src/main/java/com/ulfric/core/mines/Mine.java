package com.ulfric.core.mines;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableSet;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.region.Region;
import com.ulfric.lib.coffee.region.Shape;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.coffee.tuple.Weighted;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.block.MultiBlockChange;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.location.LocationUtils;
import com.ulfric.lib.craft.region.RegionColl;
import com.ulfric.lib.craft.world.World;
import com.ulfric.lib.craft.world.WorldUtils;

public final class Mine extends NamedBase implements Comparable<Mine> {

	public static Mine fromDocument(Document document)
	{
		Mine.Builder builder = Mine.builder();

		builder.setName(document.getString("name"));

		document.getStringList("regions").stream().map(RegionColl::getRegionByName).filter(Objects::nonNull).forEach(builder::addRegion);

		for (String type : document.getStringList("blocks"))
		{
			String[] split = type.split("\\s+");

			MaterialData data = MaterialData.of(split[0].substring("type.".length()));
			Integer weight = Integer.valueOf(split[1].substring("weight.".length()));

			builder.addType(Weighted.<MaterialData>builder().setValue(data).setWeight(weight).build());
		}

		int blockRate = document.getInteger("block-rate", 60);

		builder.setBlockRate(blockRate);

		return builder.build();
	}

	Mine(String name, int blockRate, Set<Region> regions, Set<Weighted<MaterialData>> types, int totalWeight)
	{
		super(name);
		this.regions = regions;
		this.types = types;
		this.totalWeight = totalWeight;
		this.change = new MultiBlockChange(blockRate);
	}

	private final Set<Region> regions;
	private final Set<Weighted<MaterialData>> types;
	private final int totalWeight;
	private final MultiBlockChange change;
	private AtomicBoolean resetting = new AtomicBoolean();
	private int counter;

	public boolean containsRegion(Region region)
	{
		return this.regions.contains(region);
	}

	public void increaseCounter()
	{
		this.counter++;
	}

	public int getCounter()
	{
		return this.counter;
	}

	public boolean reset()
	{
		if (this.resetting.get()) return false;

		this.resetting.set(true);

		this.counter = 0;

		ThreadUtils.runAsync(() ->
		{
			for (Region region : this.regions)
			{
				World world = WorldUtils.getWorld(region.getWorld());

				if (world == null) continue;

				Shape shape = region.getShape();

				for (Vector vector : shape)
				{
					MaterialData data = RandomUtils.randomValue(this.types, this.totalWeight);

					this.change.addBlock(LocationUtils.getLocation(world, vector), data);
				}

				ThreadUtils.run(() ->
				{
					for (Player player : PlayerUtils.getOnlinePlayers())
					{
						if (!world.equals(player.getWorld())) continue;

						if (!shape.containsPoint(player.getLocation())) continue;

						player.teleportRelative(player.getLocation().setY(shape.getMaxPoint().getIntY() + 3));
					}
				});
			}

			this.resetting.set(false);
		});

		return true;
	}

	@Override
	public int compareTo(Mine mine)
	{
		int compare = Integer.compare(this.counter, mine.counter);

		if (compare != 0) return compare;

		return this.getName().compareTo(mine.getName());
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Mine>
	{
		Builder() { }

		@Override
		public Mine build()
		{
			String localName = this.name;
			Set<Region> localRegions = this.regions.build();
			Set<Weighted<MaterialData>> localTypes = this.types.build();

			Validate.notBlank(localName);
			Validate.notEmpty(localRegions);
			Validate.notEmpty(localTypes);

			int total = 0;
			for (Weighted<MaterialData> weighted : localTypes)
			{
				total += weighted.getWeight();
			}

			return new Mine(localName, this.blockRate, localRegions, localTypes, total);
		}

		private String name;
		private int blockRate = 60;
		private final ImmutableSet.Builder<Region> regions = ImmutableSet.builder();
		private final ImmutableSet.Builder<Weighted<MaterialData>> types = ImmutableSet.builder();

		public Builder setName(String name)
		{
			Validate.notBlank(name);

			this.name = name.trim();

			return this;
		}

		public Builder setBlockRate(int blockRate)
		{
			Validate.isTrue(blockRate > 0);

			this.blockRate = blockRate;

			return this;
		}

		public Builder addType(Weighted<MaterialData> weighted)
		{
			Validate.notNull(weighted);
			Validate.notNull(weighted.getValue());

			this.types.add(weighted);

			return this;
		}

		public Builder addRegion(Region region)
		{
			Validate.notNull(region);

			this.regions.add(region);

			return this;
		}
	}

}