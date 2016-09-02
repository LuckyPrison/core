package com.ulfric.core.homes;

import org.apache.commons.lang3.Validate;

import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.location.LocationUtils;

public class Home extends NamedBase {

	private final OfflinePlayer owner;
	private final Location location;

	Home(OfflinePlayer owner, Location location, String name)
	{
		super(name);

		this.owner = owner;
		this.location = location;
	}

	public boolean canTeleport(Player player)
	{
		return player.getUniqueId().equals(this.owner.getUniqueId()) || player.hasPermission("home.teleport.others");
	}

	public void teleportTo(Player player)
	{
		player.teleport(this.location);
	}

	public OfflinePlayer getOwner()
	{
		return this.owner;
	}

	public Location getLocation()
	{
		return this.location;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Home>
	{

		private OfflinePlayer owner;
		private Location location;
		private String name;

		@Override
		public Home build()
		{
			if (this.name == null)
			{
				this.name = "home";
			}

			Validate.notNull(this.owner);

			Validate.notNull(this.location);

			return new Home(this.owner, this.location, this.name);
		}

		public Builder setOwner(OfflinePlayer owner)
		{
			Validate.notNull(owner);

			this.owner = owner;

			return this;
		}

		public Builder setLocation(Location location)
		{
			Validate.notNull(location);

			this.location = location;

			return this;
		}

		public Builder setName(String name)
		{
			this.name = name;

			return this;
		}
	}

	public void into(MutableDocument document)
	{
		MutableDocument mut = new SimpleDocument();

		mut.set("location", LocationUtils.toString(this.location));

		mut.set("name", this.getName());

		document.set(this.getName().toLowerCase(), mut);
	}

	public static Home fromDocument(OfflinePlayer owner, Document document, String name)
	{
		Document inner = document.getDocument(name.toLowerCase());

		Location location = LocationUtils.fromString(inner.getString("location"));

		String casedName = inner.getString("name");

		return builder().setOwner(owner).setLocation(location).setName(casedName).build();
	}



}
