package com.ulfric.core.teleport;

import java.util.function.Consumer;

import org.apache.commons.lang3.Validate;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.object.HashUtils;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.location.Destination;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.location.LocationUtils;

public final class Warp extends NamedBase implements Consumer<Player>, Comparable<Warp> {

	public static Warp newWarp(String name, Destination destination, ItemStack item)
	{
		return Warp.newWarp(name, destination, item, 0);
	}

	public static Warp newWarp(String name, Destination destination, ItemStack item, int visits)
	{
		Validate.notBlank(name);
		Validate.notNull(destination);

		return new Warp(name.trim(), destination, item == null ? Material.of("GRASS").toItem() : item.copy(), Math.abs(visits));
	}

	public static Warp fromDocument(String name, Document document)
	{
		ItemStack item = ItemUtils.getItem(document.getString("item"));
		int visits = document.getInteger("visits", 0);

		Document destinationDocument = document.getDocument("destination");

		Location location = LocationUtils.getLocation(destinationDocument.getString("location"));
		int delay = Math.abs(NumberUtils.getInt(destinationDocument.getInteger("delay", 0)));

		Destination destination = Destination.newDestination(location, delay);

		return Warp.newWarp(name, destination, item, visits);
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
	private boolean modified;
	private int hashCode = -1;

	public String locationToString()
	{
		return this.destination.locationToString();
	}

	@Override
	public void accept(Player player)
	{
		this.destination.accept(player);

		this.incrementVisits();
	}

	public void accept(Player player, boolean attemptRelative)
	{
		this.destination.accept(player, attemptRelative);

		this.incrementVisits();
	}

	private void incrementVisits()
	{
		this.visits++;

		this.modified = true;
	}

	public ItemStack copyItem()
	{
		return this.item.copy();
	}

	public String itemToString()
	{
		return this.item.toString();
	}

	public int getVisits()
	{
		return this.visits;
	}

	public int getDelay()
	{
		return this.destination.getDelay();
	}

	public boolean hasBeenModified()
	{
		return this.modified;
	}

	@Override
	public int compareTo(Warp other)
	{
		return Integer.compare(this.visits, other.visits);
	}

	@Override
	public int hashCode()
	{
		if (this.hashCode != -1) return this.hashCode;

		int hash = HashUtils.hash(31, this.destination);

		hash = HashUtils.hash(hash, this.item);
		hash = HashUtils.hash(hash, this.getName());

		this.hashCode = hash;

		return hash;
	}

}