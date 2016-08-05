package com.ulfric.core.teleport;

import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.ulfric.config.Document;
import com.ulfric.data.DataAddress;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.ReferenceCountedScope;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.data.DataManager;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.StringUtils;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.panel.Panel;

public final class ModuleWarps extends Module {

	public ModuleWarps()
	{
		super("warps", "Warping module", "1.0.0", "Packet");
	}

	private Map<String, Warp> warps;
	private MultiSubscription<String, Document> subscription;
	private Panel panel;

	@Override
	public void onFirstEnable()
	{
		this.warps = new CaseInsensitiveMap<>();
		this.subscription = DataManager.get()
									   .getDatabase("warps")
									   .multi(Document.class, new ReferenceCountedScope<String>(), new DataAddress<>("warps", null, null))
									   .blockOnSubscribe(true)
									   .onChange((oldValue, newValue) ->
									   {
										   String key = newValue.getAddress().getId();
										   this.warps.put(key, Warp.fromDocument(key, newValue.getValue()));
									   })
									   .subscribe();

		this.addCommand(new Command("warps", this)
		{
			@Override
			public void run()
			{
				ModuleWarps.this.displayWarpsMenu(this.getSender());
			}
		});

		this.addCommand(new Command("warp", this)
		{
			@Override
			public void run()
			{
				CommandSender sender = this.getSender();

				Warp warp = (Warp) this.getObject("warp");

				if (warp == null)
				{
					ModuleWarps.this.displayWarpsMenu(sender);

					return;
				}

				Player player = (Player) this.getObject("player");

				if (player == null)
				{
					if (!(sender instanceof Player))
					{
						sender.sendLocalizedMessage("warp.specify_player");

						return;
					}

					player = (Player) sender;
				}
				else if (!player.getUniqueId().equals(sender.getUniqueId()))
				{
					sender.sendLocalizedMessage("warp.warping_player", player.getName(), warp.getName());
				}

				player.sendLocalizedMessage("warp.warping", warp.getName(), warp.getDelay());

				if (RandomUtils.percentChance(0.20))
				{
					player.sendLocalizedMessage("warp.total_visits", warp.getName(), warp.getVisits());
				}

				warp.accept(player);
			}
		}.addOptionalArgument(Argument.builder().setPath("warp").addResolver((sen, str) ->
					{
						Warp warp = this.warps.get(str);
			
						if (warp == null)
						{
							warp = this.warps.get(StringUtils.getClosest(this.warps.keySet(), str, 3).getValue());
						}
			
						if (warp == null) return null;
			
						if (!sen.hasPermission("warps." + warp.getName())) return null;
			
						return warp;
					}).build())
		.addOptionalArgument(Argument.builder().setPath("player").setPermission("warp.others").addResolver(PlayerUtils::getOnlinePlayer).build()));
	}

	void displayWarpsMenu(CommandSender sender)
	{
		if (!(sender instanceof Player)) return;

		this.panel.open((Player) sender);
	}

	@Override
	public void onModuleEnable()
	{
		this.panel = Panel.createStandard(this.getModuleConfig("warp-menu").getRoot());

		if (!this.subscription.isSusbcribed())

		this.subscription.subscribe();
	}

	@Override
	public void onModuleDisable()
	{
		this.panel = null;

		// TODO serialize warps

		this.subscription.unsubscribe();
	}

}