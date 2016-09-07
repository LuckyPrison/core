package com.ulfric.core.economy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.MutableDocument;
import com.ulfric.core.minebuddy.Minebuddy;
import com.ulfric.core.minebuddy.ModuleMinebuddy;
import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.Patterns;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.SignListener;
import com.ulfric.lib.craft.event.player.PlayerUseSignEvent;
import com.ulfric.lib.craft.inventory.Inventory;
import com.ulfric.lib.craft.inventory.item.ItemStack;

public final class ModuleSell extends Module {

	public static final ModuleSell INSTANCE = new ModuleSell();

	private ModuleSell()
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

			priceDoc.set("a", Arrays.asList("type.STONE;6 worth.1000"));

			config.save();

			this.log("Loaded prices with default values");
		}

		Set<String> keys = priceDoc.getKeys();

		if (SetUtils.isEmpty(keys))
		{
			this.log("No keys found in document: prices");

			return;
		}

		int count = 0;
		int total = 0;

		for (String key : keys)
		{
			List<String> list = priceDoc.getStringList(key);

			if (ListUtils.isEmpty(list))
			{
				this.log("Unable to find prices in: prices." + key);

				continue;
			}

			count++;

			Map<MaterialData, Long> priced = new HashMap<>(list.size());

			for (String parse : list)
			{
				String[] split = Patterns.WHITESPACE.split(parse);

				String materialData = split[0].substring("type.".length());
				String priceData = split[1].substring("worth.".length());

				MaterialData material = MaterialData.of(materialData);

				if (material == null)
				{
					this.log("Could not parse material data: (" + materialData + ") in: prices." + key);

					continue;
				}

				Long price = NumberUtils.parseLong(priceData);

				if (price == null)
				{
					this.log("Could not parse price: (" + priceData + ") in: prices." + key);

					continue;
				}

				if (priced.put(material, price) != null)
				{
					this.log("Price specified twice in: prices." + key + " (" + materialData + ")");
				}

				total++;
			}

			if (priced.isEmpty()) continue; // TODO warning

			this.prices.put(key, priced);
		}

		if (count == 0 || total == 0)
		{
			this.log("Could not load any prices.");

			return;
		}

		if (count == 1)
		{
			if (total == 1)
			{
				this.log("Loaded 1 price in 1 category");

				return;
			}

			this.log("Loaded " + total + " prices in 1 category");

			return;
		}

		this.log("Loaded " + total + " prices across " + count + " categories");
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
				ModuleSell.this.sellAll(player, sign.getLine(1));
			}
		});
	}

	public void sellAll(Player player, String key)
	{
		String line = key;

		if (line == null)
		{
			player.sendLocalizedMessage("economy-sellall-sign-broken");

			return;
		}

		line = line.replace(" ", "");

		if (!player.hasPermission("sellsigns." + line.toLowerCase()))
		{
			player.sendLocalizedMessage("economy-sellall-missing-permission", line);

			return;
		}

		Map<MaterialData, Long> values = ModuleSell.this.prices.get(line);

		if (values == null)
		{
			player.sendLocalizedMessage("economy-sellall-missing-resource", line);

			return;
		}

		Inventory inventory;
		Minebuddy mb = ModuleMinebuddy.INSTANCE.getBuddy(player.getUniqueId());
		Player partner = null;
		double split = 0;

		if (mb != null)
		{
			partner = PlayerUtils.getPlayer(mb.getOther(player.getUniqueId()));

			if (partner != null)
			{
				inventory = partner.getInventory();
				split = mb.getSplit();
			}
			else
			{
				inventory = player.getInventory();
			}
		}

		else
		{
			inventory = player.getInventory();
		}

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
			player.sendLocalizedMessage("economy-sellall-no-items", line);

			return;
		}

		CurrencyAmount amt = CurrencyAmount.of(Currency.getDefaultCurrency(), total);
		String totalStr = amt.toFormatter().dualFormatWord().toString();

		player.sendLocalizedMessage("economy-sellall", count, totalStr);

		if (partner != null)
		{
			partner.sendLocalizedMessage("minebuddy-sold", player.getName(), count, totalStr);

			if (split == 0)
			{
				Bank.getOnlineAccount(partner.getUniqueId()).give(amt, "SellAll " + line);

				return;
			}

			if (split < 1)
			{
				long tote = (long) (amt.getAmount() * 0.9D);

				CurrencyAmount toSeller = CurrencyAmount.of(amt.getCurrency(), (long) (split * tote));
				CurrencyAmount toMiner = CurrencyAmount.of(amt.getCurrency(), (long) ((1 - split) * tote));

				Bank.getOnlineAccount(player.getUniqueId()).give(toSeller, "SellAll " + line);
				Bank.getOnlineAccount(partner.getUniqueId()).give(toMiner, "SellAll " + line);

				return;
			}
		}

		Bank.getOnlineAccount(player.getUniqueId()).give(amt, "SellAll " + line);
	}

}