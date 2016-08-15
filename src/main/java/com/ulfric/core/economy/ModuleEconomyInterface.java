package com.ulfric.core.economy;
import com.ulfric.lib.coffee.economy.BalanceChangeEvent;
import com.ulfric.lib.coffee.economy.Currency;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
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
	}

}