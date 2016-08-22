package com.ulfric.core.reward;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.ulfric.config.Document;
import com.ulfric.lib.coffee.ApiInstantiationException;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.ItemUtils;

public class Rewards {

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

	private Rewards()
	{
		throw new ApiInstantiationException();
	}

}