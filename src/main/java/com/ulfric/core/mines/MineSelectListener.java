package com.ulfric.core.mines;

import com.ulfric.core.mines.mixin.IMineSelector;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerInteractEvent;
import org.apache.commons.lang.Validate;

public class MineSelectListener extends Listener {

	public MineSelectListener(ModuleBase owner) {
		super(owner);
	}

	@Handler
	public void onClick(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (player.getMixin(IMineSelector.class) != null)
		{
			IMineSelector selector = player.as(IMineSelector.class);

			Validate.notNull(selector);

			if (selector.isSelecting())
			{
				if (!event.getBlock().getType().equals(Mines.AIR)) return;
				if (event.getAction().isLeftClick())
				{
					selector.setCornerA(event.getBlock().getLocation());
				} else if (event.getAction().isRightClick())
				{
					selector.setCornerB(event.getBlock().getLocation());
				}
			}
		}
	}
}
