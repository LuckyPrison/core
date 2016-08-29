package com.ulfric.core.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.SignListener;
import com.ulfric.lib.craft.event.player.PlayerUseSignEvent;
import com.ulfric.lib.craft.inventory.Inventory;
import com.ulfric.lib.craft.inventory.item.ItemStack;

final class ModuleSell extends Module {

	public ModuleSell()
	{
		super("sell", "responsible for selling items", "1.0.0", "Packet");
	}

	Map<String, Map<MaterialData, Long>> prices;

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = this.getModuleConfig();
		MutableDocument root = config.getRoot();
		MutableDocument priceDoc = root.getDocument("prices");

		if (priceDoc == null)
		{
			priceDoc = root.createDocument("prices");

			priceDoc = priceDoc.createDocument("example-key");

			priceDoc = priceDoc.createDocument("example");

			priceDoc.set("item", "stone;6");
			priceDoc.set("value", "1000");

			config.save();

			return;
		}

		Set<String> keys = priceDoc.getKeys();

		if (SetUtils.isEmpty(keys))
		{
			this.log("No keys found in document: prices");

			return;
		}

		for (String key : keys)
		{
			Document node = priceDoc.getDocument(key);

			if (node == null)
			{
				this.log("Invalid node: prices." + key);

				continue;
			}

			Set<String> priceKeys = node.getKeys();

			if (SetUtils.isEmpty(priceKeys))
			{
				this.log("No keys found: prices." + key);

				continue;
			}

			Map<MaterialData, Long> worths = new HashMap<>(priceKeys.size());

			for (String priceKey : priceKeys)
			{
				Document price = node.getDocument(priceKey);

				if (price == null)
				{
					this.log("Invalid node: prices." + key + '.' + priceKey);

					continue;
				}

				String dataContext = price.getString("item");

				MaterialData data = MaterialData.of(dataContext);

				if (data == null)
				{
					this.log("Invalid item: " + dataContext);

					continue;
				}

				Long worth = price.getLong("value");

				if (worth == null)
				{
					this.log("Invalid worth (should be long > 0): " + price.getString("value"));

					continue;
				}

				if (worths.put(data, worth) == null) continue;

				this.log("Found double worths: prices." + key + '.' + priceKey);
			}

			this.prices.put(key, worths);
		}
	}

	@Override
	public void onModuleDisable()
	{
		this.prices.clear();
	}

	@Override
	public void onFirstEnable()
	{
		this.prices = new CaseInsensitiveMap<>();

		this.addListener(new SignListener(this, "sellall", PlayerUseSignEvent.Action.RIGHT_CLICK)
		{
			@Override
			public void handle(Player player, Sign sign)
			{
				String line = sign.getLine(1);

				if (!player.hasPermission("sellall." + line))
				{
					player.sendLocalizedMessage("economy.sellall_missing_permission", line);

					return;
				}

				Map<MaterialData, Long> values = ModuleSell.this.prices.get(line);

				if (values == null)
				{
					player.sendLocalizedMessage("economy.sellall_missing_resource", line);

					return;
				}

				Inventory inventory = player.getInventory();

				int size = inventory.getSize();

				long total = 0;
				int count = 0;

				for (int location = 0; location < size; location++)
				{
					ItemStack item = inventory.getItem(location);

					if (item == null) continue;

					Long value = values.get(item.toMaterialData());

					if (value == null) continue;

					int amount = item.getAmount();

					total += (value * amount);
					count += amount;

					inventory.setItem(location, null);
				}

				if (total == 0)
				{
					player.sendLocalizedMessage("economy.sellall_no_items", line);

					return;
				}

				CurrencyAmount amt = CurrencyAmount.of(Currency.getDefaultCurrency(), total);

				player.sendLocalizedMessage("economy.sellall", count, amt.toFormatter().dualFormatWord());

				Bank.getOnlineAccount(player.getUniqueId()).give(amt, "SellAll " + line);
			}
		});
	}

}