package com.ulfric.core.achievement;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.panel.Button;
import com.ulfric.lib.craft.panel.Panel;
import com.ulfric.lib.craft.panel.standard.StandardPanel;

public enum Categories {

	INSTANCE;

	private final Set<Category> categories = Sets.newTreeSet();

	public void register(Category category)
	{
		this.categories.add(category);
	}

	public Category getByName(String name)
	{
		String lower = name.toLowerCase();

		for (Category category : this.categories)
		{
			if (!category.getName().toLowerCase().equals(lower)) continue;

			return category;
		}

		return null;
	}

	public void openPanel(Player player)
	{
		StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(this.categories.size(), 9), "Select a Category");

		UUID uuid = player.getUniqueId();

		int count = 0;
		for (Category category : this.categories)
		{
			Button.Builder button = Button.builder();

			button.addSlot(count++, category.toItem(uuid));
			button.addAction(event -> category.openPanel(player));

			panel.addButton(button.build());
		}

		panel.open(player);
	}

}