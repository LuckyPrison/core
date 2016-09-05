package com.ulfric.core.kit;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.ulfric.config.Document;
import com.ulfric.core.reward.Reward;
import com.ulfric.core.reward.Rewards;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.craft.entity.player.CooldownTable;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public final class Kit extends NamedBase {

	public static Kit fromDocument(Document document)
	{
		Validate.notNull(document);

		String name = document.getString("name");
		Validate.notBlank(name);

		long cooldown = document.getLong("cooldown", 0L);

		Document rewards = document.getDocument("contents");

		Reward multi = Rewards.parseMultiReward(rewards);

		return new Kit(name, multi, cooldown);
	}

	Kit(String name, Reward reward, long cooldown)
	{
		super(name);

		this.reward = reward;
		this.cooldown = cooldown;
	}

	private final Reward reward;
	private final long cooldown;

	public long give(UUID cooldownHolder, Player receiver, String reason)
	{
		if (cooldownHolder != null && this.cooldown > 0)
		{
			CooldownTable table = PlayerUtils.getCooldownTable(cooldownHolder);

			String token = "kit-" + this.getName();

			long currentCooldown = table.getCooldown(token);

			long currentTime = System.currentTimeMillis();

			if (currentCooldown > currentTime)
			{
				return currentCooldown - currentTime;
			}

			table.setCooldown(token, this.cooldown);
		}

		this.reward.give(receiver, reason);

		return 0;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Kit>
	{
		Builder() { }

		private String name;
		private long cooldown;
		private final ImmutableList.Builder<Reward> builder = ImmutableList.builder();

		@Override
		public Kit build()
		{
			Validate.notNull(this.name);

			List<Reward> rewards = this.builder.build();

			return new Kit(this.name, Rewards.multi(rewards), this.cooldown);
		}

		public Builder setName(String name)
		{
			Validate.notBlank(name);

			this.name = name;

			return this;
		}

		public Builder addReward(Reward reward)
		{
			Validate.notNull(reward);

			this.builder.add(reward);

			return this;
		}

		public Builder setCooldown(long millis)
		{
			Validate.isTrue(millis >= 0);

			this.cooldown = millis;

			return this;
		}
	}

}