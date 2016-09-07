package com.ulfric.core.modules;

import java.util.Arrays;
import java.util.List;
import java.util.function.ObjIntConsumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.ulfric.config.Document;
import com.ulfric.core.reward.Reward;
import com.ulfric.core.reward.Rewards;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.Joiner;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.Consumables;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.inventory.item.Recipe;
import com.ulfric.lib.craft.inventory.item.ShapelessRecipe;
import com.ulfric.lib.craft.note.PlayableSound;

public class ModuleCandy extends Module {

	public ModuleCandy()
	{
		super("candy", "Candies, yum!", "1.0.0", "Packet");
	}

	private List<Candy> registeredCandies;

	@Override
	public void onFirstEnable()
	{
		this.registeredCandies = Lists.newArrayList();
	}

	@Override
	public void onModuleEnable()
	{
		Document root = this.getModuleConfig().getRoot();

		Document candies = root.getDocument("candies");

		if (candies == null)
		{
			this.log("Could not load document: " + candies);

			return;
		}

		for (String key : candies.getKeys())
		{
			Document candyDoc = candies.getDocument(key);

			if (candyDoc == null)
			{
				this.log("Could not load document: " + key);

				continue;
			}

			Candy candy = this.fromDocument(candyDoc);

			if (candy == null)
			{
				this.log("Could not load candy: " + key);

				continue;
			}

			this.registeredCandies.add(candy);
		}

		this.registeredCandies.forEach(Candy::register);
	}

	@Override
	public void onModuleDisable()
	{
		this.registeredCandies.forEach(Candy::unregister);

		this.registeredCandies.clear();
	}

	private final List<String> strings = Arrays.asList("munch", "crunch");
	private final PlayableSound sound = PlayableSound.builder().setSound("ENTITY_PLAYER_BURP").setVolume(5).setPitch(4).build();
	private Candy fromDocument(Document document)
	{
		MaterialData material = MaterialData.of(document.getString("material"));
		Reward reward = document.getBoolean("multi", false) ? Rewards.parseMultiReward(document) : Rewards.parseReward(document);
		Recipe recipe = ShapelessRecipe.fromDocument(document.getDocument("recipe"));

		Validate.notNull(material);
		Validate.notNull(reward);
		Validate.notNull(recipe);

		return new Candy(material, (player, times) ->
		{
			if (times <= 1)
			{
				reward.give(player, "Candy");
			}

			Joiner joiner = Joiner.on(", ").addAfterEffect(StringUtils::capitalize);

			for (int x = 0, n = RandomUtils.randomRange(3, 5); x < n; x++)
			{
				joiner.append(RandomUtils.randomValue(this.strings));
			}

			player.sendLocalizedMessage("candy-use", joiner.toString());

			player.playSound(this.sound);

			// TODO support times on all the reward types, not just potions
			reward.give(player, "Candy", "times", times);
		}, recipe);
	}

	private class Candy
	{
		Candy(MaterialData data, ObjIntConsumer<Player> consumer, Recipe recipe)
		{
			this.data = data;
			this.consumer = consumer;
			this.recipe = recipe;
		}

		private final MaterialData data;
		private final ObjIntConsumer<Player> consumer;
		private final Recipe recipe;

		public void register()
		{
			Consumables.register(this.data, this.consumer);
			ItemUtils.registerRecipe(this.recipe);
		}

		public void unregister()
		{
			Consumables.clear(this.data);
			ItemUtils.removeRecipe(this.recipe);
		}
	}

}