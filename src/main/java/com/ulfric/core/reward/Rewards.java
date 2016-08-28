package com.ulfric.core.reward;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.ApiInstantiationException;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.script.Script;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;
import com.ulfric.lib.craft.potion.PotionEffect;
import com.ulfric.lib.craft.potion.PotionUtils;

public class Rewards {

	public static Reward parseMultiReward(Document document)
	{
		Validate.notNull(document);

		ImmutableList.Builder<Reward> parsedRewards = ImmutableList.builder();

		for (String key : document.getKeys(false))
		{
			Document rewardDoc = document.getDocument(key);

			if (rewardDoc == null) continue;

			Reward reward = Rewards.parseReward(rewardDoc);

			if (reward == null) continue;

			parsedRewards.add(reward);
		}

		List<Reward> builtRewards = parsedRewards.build();

		Validate.notEmpty(builtRewards);

		return Rewards.multi(builtRewards);
	}

	public static Reward parseReward(Document document)
	{
		Validate.notNull(document);

		String type = document.getString("type", "item");

		switch(type.toLowerCase())
		{
			case "item":
				return Rewards.newItemReward(document);

			case "items":
			case "multiitem":
			case "multi-item":
				return Rewards.newItemMultiReward(document);

			case "potion":
				return Rewards.newPotionReward(document);

			case "potions":
			case "multipotion":
			case "multi-potion":
				return Rewards.newPotionMultiReward(document);

			case "cmd":
			case "command":
				return Rewards.newCommandReward(document);

			case "cmds":
			case "commands":
			case "multicmd":
			case "multicommand":
			case "multi-cmd":
			case "multi-command":
				return Rewards.newMultiCommandReward(document);

			case "cash":
			case "money":
				return Rewards.newMoneyReward(document);

			case "multicash":
			case "multimoney":
			case "multi-cash":
			case "multi-money":
				return Rewards.newMoneyMultiReward(document);

			case "script":
				return Rewards.newScriptReward(document);

			case "multiscript":
			case "multi-script":
				return Rewards.newScriptMultiReward(document);

			default:
				throw new IllegalArgumentException(type);
		}
	}

	public static Reward multi(List<Reward> rewards)
	{
		return MultiReward.valueOf(rewards);
	}

	private static Reward newItemReward(Document document)
	{
		ItemStack item = ItemUtils.getItem(document.getString("item"));

		Validate.notNull(item);

		return ItemReward.valueOf(item);
	}

	private static Reward newItemMultiReward(Document document)
	{
		List<String> strings = document.getStringList("items");

		Validate.notEmpty(strings);

		List<ItemStack> items = strings.stream().map(ItemUtils::getItem).filter(Objects::nonNull).collect(Collectors.toList());

		return MultiItemReward.valueOf(items);
	}

	private static Reward newPotionReward(Document document)
	{
		PotionEffect potion = PotionUtils.of(document.getString("potion"));

		Validate.notNull(potion);

		return PotionReward.valueOf(potion);
	}

	private static Reward newPotionMultiReward(Document document)
	{
		List<String> strings = document.getStringList("potions");

		Validate.notEmpty(strings);

		List<PotionEffect> effects = strings.stream().map(PotionUtils::of).filter(Objects::nonNull).collect(Collectors.toList());

		return MultiPotionReward.valueOf(effects);
	}

	private static Reward newCommandReward(Document document)
	{
		String command = document.getString("command");

		return CommandReward.valueOf(command);
	}

	private static Reward newMultiCommandReward(Document document)
	{
		List<String> strings = document.getStringList("commands");

		return MultiCommandReward.valueOf(strings);
	}

	private static Reward newMoneyReward(Document document)
	{
		String amount = document.getString("amount");

		CurrencyAmount camount = CurrencyAmount.valueOf(amount);

		return MoneyReward.valueOf(camount);
	}

	private static Reward newMoneyMultiReward(Document document)
	{
		List<String> strings = document.getStringList("amounts");

		Validate.notEmpty(strings);

		List<CurrencyAmount> amounts = strings.stream().map(CurrencyAmount::valueOf).filter(Objects::nonNull).collect(Collectors.toList());

		return MultiMoneyReward.valueOf(amounts);
	}

	private static Reward newScriptReward(Document document)
	{
		String name = document.getString("script");

		Script script = Script.byName(name);

		return ScriptReward.valueOf(script);
	}

	private static Reward newScriptMultiReward(Document document)
	{
		List<String> strings = document.getStringList("scripts");

		Validate.notEmpty(strings);

		List<Script> scripts = strings.stream().map(Script::byName).filter(Objects::nonNull).collect(Collectors.toList());

		return MultiScriptReward.valueOf(scripts);
	}

	private Rewards()
	{
		throw new ApiInstantiationException();
	}

}