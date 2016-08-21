package com.ulfric.core.economy;
import java.util.UUID;

import com.ulfric.core.achievement.Achievement;
import com.ulfric.core.achievement.Categories;
import com.ulfric.core.achievement.Category;
import com.ulfric.lib.coffee.economy.BalanceChangeEvent;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.scoreboard.Scoreboard;
import com.ulfric.lib.craft.scoreboard.ScoreboardElement;

public class ModuleEconomyInterface extends Module {

	public ModuleEconomyInterface()
	{
		super("economy-interface", "Economy interfacing; the pay command, balance signs, etc.", "1.0.0", "Packet and evilmidget38");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandBalance(this));
		this.addCommand(new CommandPay(this));

		this.addListener(new SignBalance(this));

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Scoreboard scoreboard = event.getPlayer().getScoreboard();
				scoreboard.addElement(new ElementBalance(scoreboard));
			}

			@Handler
			public void onBalance(BalanceChangeEvent event)
			{
				if (!event.getCurrency().equals(Currency.getDefaultCurrency())) return;

				Player player = PlayerUtils.getPlayer(event.getAccount().getUniqueId());

				if (player == null) return;

				ScoreboardElement element = player.getScoreboard().elementFromClazz(ElementBalance.class);

				if (element == null) return;

				element.update(player);
			}
		});

		MaterialData reed = MaterialData.of(Material.of("SUGAR_CANE"));
		Category category = Category.builder().setName("Economy").setItem(reed).build();

		Achievement million1 = Achievement.builder().setName("Small Loan").setCode("smallloan").setDescription("Acquire $1,000,000").setItem(reed).build();
		category.addAchievement(million1);

		Achievement million2 = Achievement.builder().setName("The Wolf of Wall Street").setCode("wolfofwallstreet").setDescription("Acquire $51,000,000").setItem(reed).build();
		category.addAchievement(million2);

		Achievement billion = Achievement.builder().setName("Mr. Burns").setCode("mrburns").setDescription("Acquire $16,800,000,000").setItem(reed).build();
		category.addAchievement(billion);

		Achievement trillion = Achievement.builder().setName("lucky1baltop").setCode("lucky1baltop").setDescription("Acquire $1,000,000,000,000").setItem(reed).build();
		category.addAchievement(trillion);

		Categories.INSTANCE.register(category);

		this.addListener(new Listener(this)
		{
			@Handler
			public void onBalance(BalanceChangeEvent event)
			{
				if (!event.getCurrency().equals(Currency.getDefaultCurrency())) return;

				UUID uuid = event.getAccount().getUniqueId();

				long bal = event.getNewBalance();

				if (bal >= 1_000_000_000_000L)
				{
					trillion.increment(uuid);
				}
				else if (bal >= 16_800_000_000L)
				{
					billion.increment(uuid);
				}
				else if (bal >= 51_000_000)
				{
					million2.increment(uuid);
				}
				else if (bal >= 1_000_000)
				{
					million1.increment(uuid);
				}
			}
		});
	}

}