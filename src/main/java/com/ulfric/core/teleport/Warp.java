package com.ulfric.core.teleport;

import java.util.function.Consumer;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.Destination;

public final class Warp extends NamedBase implements Consumer<Player>, Comparable<Warp> {

	public static Warp newWarp(String name, Destination destination, ItemStack item)
	{
		return Warp.newWarp(name, destination, item, 0);
	}

	public static Warp newWarp(String name, Destination destination, ItemStack item, int visits)
	{
		Validate.notBlank(name);
		Validate.notNull(destination);

		return new Warp(name.trim(), destination, item == null ? Material.of("GRASS").toItem() : item, Math.abs(visits));
	}

	private Warp(String name, Destination destination, ItemStack item)
	{
		this(name, destination, item, 0);
	}

	private Warp(String name, Destination destination, ItemStack item, int visits)
	{
		super(name);
		this.destination = destination;
		this.item = item;
		this.visits = visits;
	}

	private final Destination destination;
	private final ItemStack item;
	private int visits;

	@Override
	public void accept(Player player)
	{
		this.destination.accept(player);

		this.visits++;
	}

	public ItemStack copyItem()
	{
		return this.item.copy();
	}

	public int getVisits()
	{
		return this.visits;
	}

	public int getDelay()
	{
		return this.destination.getDelay();
	}

	@Override
	public int compareTo(Warp other)
	{
		return Integer.compare(this.visits, other.visits);
	}

}