package com.ulfric.core.modules;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.block.SignChangeEvent;
import com.ulfric.lib.craft.string.ChatUtils;

public final class ModuleColoredSigns extends Module {

	public ModuleColoredSigns()
	{
		super("colored-signs", "Color me signs!", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onSign(SignChangeEvent event)
			{
				Player player = event.getPlayer();

				if (!player.hasPermission("signs.color")) return;

				for (int index = 0; index < 4; index++)
				{
					event.setLine(index, ChatUtils.color(event.getLine(index)));
				}
			}
		});
	}

}