package com.ulfric.core.settings;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.craft.inventory.item.ItemStack;

public final class State {

	State(String text, ItemStack item)
	{
		this.text = text;
		this.item = item;
	}

	private final String text;
	private final ItemStack item;

	public String getText()
	{
		return this.text;
	}

	public ItemStack getItem()
	{
		if (this.item == null) return null;

		return this.item.copy();
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<State>
	{
		Builder() { }

		private String text;
		private ItemStack item;

		@Override
		public State build()
		{
			Validate.notBlank(this.text);

			return new State(this.text, this.item);
		}

		public Builder setText(String text)
		{
			Validate.notBlank(text);

			this.text = text;

			return this;
		}

		public Builder setItem(ItemStack item)
		{
			Validate.notNull(item);

			this.item = item;

			return this;
		}
	}

}