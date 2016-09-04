package com.ulfric.core.achievement;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.Named;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.panel.Panel;
import com.ulfric.lib.craft.panel.standard.StandardPanel;
import com.ulfric.lib.craft.string.ChatUtils;

public final class Category implements Named, Comparable<Category> {

	Category(String name, int priority, MaterialData item)
	{
		this.name = name;
		this.priority = priority;
		this.item = item;
	}

	private final String name;
	private final int priority;
	private final MaterialData item;
	private final Set<Achievement> achievements = Sets.newLinkedHashSet();

	@Override
	public String getName()
	{
		return this.name;
	}

	public ItemStack toItem(UUID uuid)
	{
		ItemStack to = this.item.toItem(1);

		ItemMeta meta = to.getMeta();

		List<String> lore = Lists.newArrayListWithCapacity(2);

		lore.add("");
		lore.add(ChatUtils.color(Strings.format("{0}% Complete", this.completePercentage(uuid))));

		meta.setAllLore(lore);

		meta.setDisplayName(ChatUtils.color("&7") + this.name);

		to.setMeta(meta);

		return to;
	}

	public void addAchievement(Achievement achievement)
	{
		Validate.notNull(achievement);

		this.achievements.add(achievement);
	}

	private int completePercentage(UUID uuid)
	{
		if (this.achievements.isEmpty()) return 100;

		float complete = 0;

		for (Achievement achievement : this.achievements)
		{
			if (!achievement.isComplete(uuid)) continue;

			complete++;
		}

		return (int) (complete / this.achievements.size()) * 100;
	}

	public void openPanel(Player player)
	{
		Validate.notNull(player);

		if (this.achievements.isEmpty()) return;

		UUID uuid = player.getUniqueId();
		Locale locale = player.getLocale();

		StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(this.achievements.size(), 9), locale.getFormattedMessage("achievement.panel", this.completePercentage(uuid)));

		for (Achievement achievement : this.achievements)
		{
			panel.addItem(achievement.toItem(uuid));
		}

		panel.open(player);
	}

	@Override
	public int compareTo(Category other)
	{
		int compare = Integer.compare(this.priority, other.priority);
		if (compare != 0) return compare;
		return this.name.compareTo(other.name);
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Category>
	{
		Builder() { }

		private String name;
		private int priority;
		private MaterialData item;

		@Override
		public Category build()
		{
			Validate.notNull(this.name);
			Validate.notNull(this.item);

			return new Category(this.name, this.priority, this.item);
		}

		public Builder setName(String name)
		{
			Validate.notBlank(name);

			this.name = name;

			return this;
		}

		public Builder setPriority(int priority)
		{
			this.priority = priority;

			return this;
		}

		public Builder setItem(MaterialData item)
		{
			Validate.notNull(item);

			this.item = item;

			return this;
		}
	}

}