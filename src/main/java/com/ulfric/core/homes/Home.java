package com.ulfric.core.homes;

import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.craft.location.Location;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;

public class Home extends NamedBase {

	private final OfflinePlayer owner;
	private final Location location;
	private final String name;

	Home(OfflinePlayer owner, Location location, String name)
	{
		super(name);

		this.owner = owner;
		this.location = location;
		this.name = name;
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



}
