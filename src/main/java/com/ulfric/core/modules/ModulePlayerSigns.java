package com.ulfric.core.modules;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.data.DataAddress;
import com.ulfric.data.ListSubscription;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.block.Block;
import com.ulfric.lib.craft.block.BlockState;
import com.ulfric.lib.craft.block.Sign;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.block.BlockBreakEvent;
import com.ulfric.lib.craft.event.block.SignChangeEvent;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.sign.Signs;

public class ModulePlayerSigns extends Module {

	public ModulePlayerSigns()
	{
		super("player-signs", "Replace {player} with the player name on a sign", "1.0.0", "Packet");
	}

	List<Block> signs;
	Set<Location> removedSigns;
	private ListSubscription subscription;

	@Override
	public void onModuleEnable()
	{
		if (!this.subscription.isSubscribed())
		{
			this.subscription.subscribe();
		}
	}

	@Override
	public void onModuleDisable()
	{
		if (this.subscription.isSubscribed())
		{
			this.subscription.unsubscribe();
		}
	}

	@Override
	public void onFirstEnable()
	{
		this.subscription = DataManager.get().getDatabase("signs").list(new DataAddress<String>("playersigns", null, null)).subscribe();

		this.signs = Lists.newArrayList();
		this.removedSigns = Sets.newHashSet();

		this.addListener(new Listener(this)
		{
			@Handler
			public void onJoin(PlayerJoinEvent event)
			{
				Player player = event.getPlayer();
				String name = player.getName();

				Iterator<Block> iterator = ModulePlayerSigns.this.signs.iterator();

				while (iterator.hasNext())
				{
					Block signBlock = iterator.next();

					BlockState state = signBlock.getState();

					if (!(state instanceof Sign))
					{
						iterator.remove();

						ModulePlayerSigns.this.removedSigns.add(signBlock.getLocation());

						continue;
					}

					Sign sign = (Sign) state;

					String[] lines = sign.getLines();

					int length = lines.length;

					for (int x = 0; x < length; x++)
					{
						lines[x] = lines[x].replace("{player}", name);
					}

					player.sendSign(sign.getLocation(), lines);
				}
			}

			@Handler(ignoreCancelled = true)
			public void onSign(SignChangeEvent event)
			{
				if (!event.getPlayer().hasPermission("playersigns.create")) return;

				for (String line : event.getLines())
				{
					if (!line.contains("{player}")) continue;

					Block block = event.getBlock();

					ModulePlayerSigns.this.signs.add(block);

					ModulePlayerSigns.this.removedSigns.remove(block.getLocation());

					break;
				}
			}

			@Handler(ignoreCancelled = true)
			public void onBreak(BlockBreakEvent event)
			{
				Block block = event.getBlock();

				if (!Signs.isSign(block)) return;

				ModulePlayerSigns.this.removedSigns.remove(block.getLocation());
			}
		});
	}

	

}