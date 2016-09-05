package com.ulfric.core.luckyblocks;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.ulfric.core.reward.Reward;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.coffee.tuple.Weighted;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.Player;

public abstract class LuckyBlock extends NamedBase {

	public static boolean isLuckyBlock(Block block)
	{
		return ModuleLuckyBlocks.INSTANCE.isLuckyBlock(block);
	}

	LuckyBlock(String name, MaterialData data, List<Weighted<Reward>> rewards, int weight)
	{
		super(name);

		this.data = data;
		this.rewards = rewards;
		this.weight = weight;
	}

	private final MaterialData data;
	private final List<Weighted<Reward>> rewards;
	private final int weight;

	public final MaterialData getData()
	{
		return this.data;
	}

	public final void use(Player player, Block block)
	{
		RandomUtils.randomValue(this.rewards, this.weight).give(player, "LuckyBlock Drop", "block", block);
	}

	public abstract boolean canUse(Player player);

	public abstract String getErrorMessage();

	public abstract long getErrorMillisDelay();

	private static final class PublicLuckyBlock extends LuckyBlock
	{
		PublicLuckyBlock(String name, MaterialData data, List<Weighted<Reward>> rewards, int weight)
		{
			super(name, data, rewards, weight);
		}

		@Override
		public boolean canUse(Player player)
		{
			return true;
		}

		@Override
		public String getErrorMessage()
		{
			throw new UnsupportedOperationException("Public lucky blocks don't have an error message");
		}

		@Override
		public long getErrorMillisDelay()
		{
			throw new UnsupportedOperationException("Public lucky blocks don't have an error delay");
		}
	}

	private static final class PermissibleLuckyBlock extends LuckyBlock
	{
		PermissibleLuckyBlock(String name, MaterialData data, List<Weighted<Reward>> rewards, int weight, String permission, String errorMessage, long errorMillisDelay)
		{
			super(name, data, rewards, weight);

			this.permission = permission;
			this.errorMessage = errorMessage;
			this.errorMillisDelay = errorMillisDelay;
		}

		private final String permission;
		private final String errorMessage;
		private final long errorMillisDelay;

		@Override
		public boolean canUse(Player player)
		{
			return player.hasPermission(this.permission);
		}

		@Override
		public String getErrorMessage()
		{
			return this.errorMessage;
		}

		@Override
		public long getErrorMillisDelay()
		{
			return this.errorMillisDelay;
		}
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<LuckyBlock>
	{
		Builder() { }

		private String name;
		private MaterialData type;
		private String permission;
		private String permissionError;
		private long errorDelay;
		private ImmutableList.Builder<Weighted<Reward>> rewards = ImmutableList.builder();
		private int totalWeight;

		@Override
		public LuckyBlock build()
		{
			Validate.notNull(this.name);
			Validate.notNull(this.type);

			List<Weighted<Reward>> builtRewards = this.rewards.build();

			Validate.notEmpty(builtRewards);

			if (this.permission == null)
			{
				return new PublicLuckyBlock(this.name, this.type, builtRewards, this.totalWeight);
			}

			Validate.notNull(this.permissionError);

			return new PermissibleLuckyBlock(this.name, this.type, builtRewards, this.totalWeight, this.permission, this.permissionError, this.errorDelay);
		}

		public Builder setName(String name)
		{
			Validate.notBlank(name);

			this.name = name;

			return this;
		}

		public Builder setType(MaterialData type)
		{
			Validate.notNull(type);

			this.type = type;

			return this;
		}

		public Builder setPermission(String permission)
		{
			Validate.notBlank(permission);

			this.permission = permission;

			return this;
		}

		public Builder setPermissionError(String permissionError)
		{
			Validate.notBlank(permissionError);

			this.permissionError = permissionError;

			return this;
		}

		public Builder setPermissionErrorDelayInMillis(long millis)
		{
			Validate.isTrue(millis >= 0);

			this.errorDelay = millis;

			return this;
		}

		public Builder addReward(int weight, Reward reward)
		{
			Validate.isTrue(weight >= 0);
			Validate.notNull(reward);

			this.totalWeight += weight;
			this.rewards.add(Weighted.<Reward>builder().setWeight(weight).setValue(reward).build());

			return this;
		}
	}

}