package com.ulfric.core.settings;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ulfric.data.DataAddress;
import com.ulfric.data.DocumentStore;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.data.scope.ScopeListener;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.inventory.item.ItemStack;

public final class Setting extends NamedBase implements Comparable<Setting>, ScopeListener<UUID> {

	Setting(String name, int priority, List<State> states, ItemStack item, String description, Map<UUID, Integer> cache, MultiSubscription<UUID, Integer> subscription)
	{
		super(name);

		this.priority = priority;
		this.states = states;
		this.cache = cache;
		this.subscription = subscription;
		this.item = item;
		this.description = description;
	}

	private final int priority;
	private final List<State> states;
	private final Map<UUID, Integer> cache;
	private final ItemStack item;
	private final String description;
	private final MultiSubscription<UUID, Integer> subscription;

	public ItemStack getItem()
	{
		return this.item.copy();
	}

	public String getDescription()
	{
		return this.description;
	}

	@Override
	public void onAddition(UUID uuid)
	{
		Integer value = this.subscription.get(uuid).getValue();

		if (value == null) return;

		this.cache.put(uuid, value);
	}

	@Override
	public void onRemove(UUID uuid)
	{
		this.cache.remove(uuid);
	}

	public void subscribe()
	{
		this.subscription.subscribe();
	}

	public void unsubscribe()
	{
		this.subscription.unsubscribe();
	}

	public void setData(UUID uuid, Integer data)
	{
		Validate.notNull(uuid);

		Integer dataOrDefault = data == null ? 0 : (data < 0 ? 0 : data);

		this.cache.put(uuid, dataOrDefault);
		this.subscription.get(uuid).setValue(dataOrDefault);
	}

	public Integer getData(UUID uuid)
	{
		Validate.notNull(uuid);

		return NumberUtils.getInt(this.cache.get(uuid));
	}

	public State getState(Integer integer)
	{
		int value = integer == null ? 0 : integer;

		int size = this.states.size();

		if (size <= value)
		{
			return this.states.get(size - 1);
		}

		return this.states.get(value);
	}

	public State getState(UUID uuid)
	{
		return this.getState(this.getData(uuid));
	}

	public State getNextState(State state)
	{
		for (State element : this.states)
		{
			if (state != element) continue;

			return element;
		}

		return this.states.get(0);
	}

	public Integer getNextData(Integer integer)
	{
		if (integer == null) return 1;

		int size = this.states.size();

		if (integer >= size) return 0;

		return integer + 1;
	}

	@Override
	public int compareTo(Setting setting)
	{
		int compare = Integer.compare(this.priority, setting.priority);

		if (compare != 0) return compare;

		return this.getName().compareTo(setting.getName());
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == null) return false;

		if (object == this) return true;

		if (!(object instanceof Setting)) return false;

		Setting other = (Setting) object;

		return this.getName().equals(other.getName());
	}

	@Override
	public int hashCode()
	{
		return this.getName().hashCode();
	}

	@Override
	public String toString()
	{
		return Strings.format("Setting[name={0}, priority={1}]", this.getName(), this.priority);
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Setting>
	{
		Builder() { }

		@Override
		public Setting build()
		{
			Validate.notBlank(this.name);

			List<State> builtStates = this.states.build();

			Validate.isTrue(builtStates.size() >= 2, "You must specify at least two states!");

			Map<UUID, Integer> data = Maps.newHashMap();

			DocumentStore database = PlayerUtils.getPlayerData();

			MultiSubscription<UUID, Integer> subscription = database.multi(Integer.class, PlayerScopes.ONLINE, new DataAddress<>("settings", null, this.name)).blockOnSubscribe(true).subscribe();

			Validate.notNull(this.item);

			Validate.notBlank(this.description);

			return new Setting(this.name, this.priority, builtStates, this.item, this.description, data, subscription);
		}

		private String name;
		private int priority;
		private ImmutableList.Builder<State> states = ImmutableList.builder();
		private ItemStack item;
		private String description;

		public Builder setName(String name)
		{
			Validate.notBlank(name);

			this.name = name.trim();

			return this;
		}

		public Builder setPriority(int priority)
		{
			this.priority = priority;

			return this;
		}

		public Builder addState(State state)
		{
			Validate.notNull(state);

			this.states.add(state);

			return this;
		}

		public Builder setItem(ItemStack item)
		{
			Validate.notNull(item);

			this.item = item;

			return this;
		}

		public Builder setDescription(String description)
		{
			Validate.notBlank(description);

			this.description = description.trim();

			return this;
		}
	}

}