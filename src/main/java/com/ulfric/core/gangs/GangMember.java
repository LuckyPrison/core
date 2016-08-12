package com.ulfric.core.gangs;

import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.string.Named;
import com.ulfric.lib.coffee.string.Unique;
import com.ulfric.lib.craft.entity.player.OfflinePlayer;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public final class GangMember implements Named, Unique {

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder implements org.apache.commons.lang3.builder.Builder<GangMember>
	{
		Builder() { }

		@Override
		public GangMember build()
		{
			Validate.notNull(this.uuid);
			Validate.notNull(this.joined);
			Validate.notNull(this.gang);

			return new GangMember(this.uuid, this.joined, this.gang, this.rank);
		}

		private UUID uuid;
		private Instant joined;
		private Gang gang;
		private GangRank rank;

		public Builder setUUID(UUID uuid)
		{
			this.uuid = uuid;

			return this;
		}

		public Builder setJoined(Instant joined)
		{
			this.joined = joined;

			return this;
		}

		public Builder setGang(Gang gang)
		{
			this.gang = gang;

			return this;
		}

		public Builder setRank(GangRank rank)
		{
			this.rank = rank;

			return this;
		}
	}

	GangMember(UUID uuid, Instant joined, Gang gang, GangRank rank)
	{
		this.uuid = uuid;
		this.joined = joined;
		this.gang = gang;
		this.rank = rank;
	}

	private final UUID uuid;
	private final Instant joined;
	private final Gang gang;
	private final GangRank rank;

	@Override
	public UUID getUniqueId()
	{
		return this.uuid;
	}

	public Instant getJoined()
	{
		return this.joined;
	}

	public Gang getGang()
	{
		return this.gang;
	}

	public GangRank getRank()
	{
		return this.rank;
	}

	@Override
	public String getName()
	{
		return PlayerUtils.getOfflinePlayer(this.uuid).getName();
	}

	public Player toPlayer()
	{
		return PlayerUtils.getPlayer(this.uuid);
	}

	public OfflinePlayer toOfflinePlayer()
	{
		return PlayerUtils.getOfflinePlayer(this.uuid);
	}

}