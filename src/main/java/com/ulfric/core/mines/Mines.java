package com.ulfric.core.mines;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.location.Vector;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.tuple.Weighted;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.location.LocationUtils;
import com.ulfric.lib.craft.world.World;
import com.ulfric.lib.data.document.Document;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Mines {

	public static final Material AIR = Material.of("air");
	public static final Pattern WEIGHTS = Pattern.compile("([A-z]+):([0-9]+)");
	public static final Argument MINE_NAME = Argument.builder().setUsage("core.mines.name.usage").setPath("name").addResolver((s, a) -> a).build();
	public static final Argument MINE_PERMISSONS = Argument.builder().setUsage("core.mines.permissions.usage").setPath("permissions").addResolver((s, perms) -> Sets.newHashSet(perms.split(";"))).setDefaultValue((c) -> "core.mines.permissions." + c.getObject("name")).build();
	public static final Argument MINE_CONTENTS = Argument.builder().setUsage("core.mines.contents.usage").setPath("contents").addResolver((sender, contents) ->
			Arrays.stream(contents.split(";")).map(str ->
			{
				Matcher m = WEIGHTS.matcher(str);
				if (m.matches())
				{
					Material material = Material.of(m.group(1));
					if (material == null)
					{
						sender.sendLocalizedMessage("core.mines.material_not_found", m.group(1));
						return null;
					}
					int weight;
					try
					{
						weight = Integer.parseInt(m.group(2));
					} catch (NumberFormatException e) {
						sender.sendLocalizedMessage("core.mines.invalid_number", m.group(2));
						return null;
					}
					return Weighted.builder().setValue(material).setWeight(weight).build();
				}
				else
				{
					sender.sendLocalizedMessage("core.mines.malformed_composition", str);
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toSet())
	).build();

	private Map<String, Mine> mines;
	private World mineWorld;

	public Mines(Document mineDoc, World mineWorld, long resetInterval)
	{
		this.mines = Maps.newHashMapWithExpectedSize(mineDoc.getKeys(false).size());
		this.mineWorld = mineWorld;
		ThreadUtils.runRepeating(new MineResetTask(), resetInterval * 20);
	}

	public void addMine(@Nonnull Mine mine)
	{
		this.mines.put(mine.getName(), mine);
	}

	public Mine getMine(String name)
	{
		return this.mines.get(name);
	}

	public void removeMine(String name)
	{
		if (this.mines.containsKey(name)) this.mines.remove(name);
	}

	public boolean mineExists(String name)
	{
		return this.mines.containsKey(name);
	}

	class MineResetTask implements Runnable {

		@Override
		public void run()
		{
			Map<Player, Set<String>> messages = new HashMap<>();
			for (Mine mine : mines.values())
			{
				int totalWeight = mine.getContents().stream().mapToInt(Weighted::getWeight).sum();
				MultiBlockChange operation = new MultiBlockChange();
				mine.getRegion().getCuboid().forEach(v -> operation.addBlock(v, RandomUtils.randomValue(mine.getContents(), totalWeight)));
				ThreadUtils.runAsync(operation);
				PlayerUtils.getOnlinePlayers().stream().filter(mine::hasPermission).forEach(p ->
				{
					if (messages.containsKey(p))
					{
						messages.put(p, Sets.newHashSet(mine.getName()));
					}
					else
					{
						messages.get(p).add(mine.getName());
					}

					if (mine.getRegion().getCuboid().containsPoint(p.getLocation()))
					{
						Location loc = LocationUtils.getLocationAt(p.getWorld(), p.getLocation().getIntX(), mine.getRegion().getCuboid().getMaxPoint().getIntY(), p.getLocation().getIntZ());

						Validate.notNull(loc);

						p.teleport(loc);
					}
				});
			}
			messages.forEach((player, mines) -> player.sendLocalizedMessage("core.mines.reset", StringUtils.join(messages.values(), ", ")));
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
			blocks.forEach(p ->
			{
				Block b = mineWorld.getBlock(p.getKey());
				if (b != null)
				{
					b.setType(p.getValue());
				}
			});
		}
	}
}
