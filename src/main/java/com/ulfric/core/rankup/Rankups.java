package com.ulfric.core.rankup;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Permissions;
import com.ulfric.lib.coffee.npermission.Track;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.block.MaterialData;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.ClickType;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.panel.Button;
import com.ulfric.lib.craft.panel.Panel;
import com.ulfric.lib.craft.panel.standard.StandardPanel;

public enum Rankups {

	INSTANCE;

	private final Map<Track, TrackPrices> rankups = Maps.newHashMap();
	final Map<Track, MaterialData> trackItems = Maps.newHashMap();
	Track defaultTrack;

	public Rankup getActive(Player player)
	{
		Track track = player.getCurrentTrack();
		if (track == null)
		{
			track = this.defaultTrack;

			if (track == null)
			{
				return null;
			}
		}

		Group next = player.getNextGroup();
		Group current = player.getCurrentGroup();

		if (next != null)
		{
			return this.newRankup(track, current, next);
		}

		if (current == null)
		{
			return null;
		}

		return this.newRankup(track, null, current);
	}

	private Rankup newRankup(Track track, Group oldGroup, Group newGroup)
	{
		TrackPrices costs = this.rankups.get(track);
		return new Rankup(track, oldGroup, newGroup, costs == null ? null : costs.getPrice(newGroup));
	}

	void registerRankup(Group group, CurrencyAmount amount)
	{
		for (Track track : Permissions.getTracks(group))
		{
			TrackPrices amounts = this.rankups.get(track);

			if (amounts == null)
			{
				amounts = new TrackPrices();

				this.rankups.put(track, amounts);
			}

			amounts.putPrice(group, amount);
		}
	}

	void clear()
	{
		this.rankups.clear();
		this.trackItems.clear();
	}

	public void openPanel(Player player)
	{
		Locale locale = player.getLocale();
		StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(this.trackItems.size(), 9), locale.getRawMessage("tracks.panel"));

		int index = 0;

		Track currentTrack = player.getCurrentTrack();

		String title = locale.getRawMessage("track.panel_item_title");
		String rightClick = locale.getRawMessage("track.panel_right_click");
		String leftClick = locale.getRawMessage("track.panel_left_click");
		String completeMessage = locale.getRawMessage("track.complete");

		Collection<Group> parents = player.getParents();

		for (Entry<Track, MaterialData> entry : this.trackItems.entrySet())
		{
			ItemStack item = entry.getValue().toItem();

			if (item == null) continue;

			Track track = entry.getKey();

			ItemMeta meta = item.getMeta();

			meta.setDisplayName(Strings.format(title, track.getName()));

			List<String> lore = Lists.newArrayList(Strings.EMPTY);

			Button.Builder button = Button.builder();

			if (track.equals(currentTrack))
			{
				lore.add(player.getLocalizedMessage("track.panel_current_track"));

				lore.add(Strings.EMPTY);

				lore.add(rightClick);

				button.addAction(event ->
				{
					Player clicked = event.getPlayer();

					ClickType click = event.getClickType();

					if (!click.isRightClick()) return;

					this.openTrackPanel(track, clicked, parents);
				});
			}
			else
			{
				int complete = 0;

				List<Group> groups = track.getGroups();

				for (Group group : groups)
				{
					if (!parents.contains(group)) continue;

					complete++;
				}

				int total = groups.size();

				lore.add(Strings.format(completeMessage, NumberUtils.percentage(total, complete)));

				lore.add(Strings.EMPTY);

				if (complete < total)
				{
					lore.add(leftClick);
				}

				lore.add(rightClick);

				button.addAction(event ->
				{
					Player clicked = event.getPlayer();

					ClickType click = event.getClickType();

					if (click.isRightClick())
					{
						this.openTrackPanel(track, clicked, parents);
					}

					else if (click.isLeftClick())
					{
						TrackPrices trackPrices = this.rankups.get(track);

						UUID uuid = clicked.getUniqueId();

						Group first = trackPrices.getFirstGroup();
						CurrencyAmount amount = trackPrices.getFirstPrice();
						if (!parents.contains(first))
						{
							if (amount != null)
							{
								BankAccount account = Bank.getOnlineAccount(uuid);

								long balance = account.getBalance(amount.getCurrency());

								if (balance < amount.getAmount())
								{
									clicked.closeInventory();

									clicked.sendLocalizedMessage("tracks.cannot_afford", track.getName(), amount.toFormatter().dualFormatWord());

									return;
								}

								account.take(amount, "Track purchase " + track.getName());
							}

							clicked.addGroup(first);
						}
						// TODO charge player to join the track

						clicked.setCurrentTrack(track);

						clicked.closeInventory();

						clicked.sendLocalizedMessage("tracks.set", track.getName());
					}
				});
			}

			item.setMeta(meta);

			button.addSlot(index++, item);

			panel.addButton(button.build());
		}
	}

	public void openTrackPanel(Track track, Player player, Collection<Group> parents)
	{
		TrackPrices map = this.rankups.get(track);

		if (map == null) return;

		Locale locale = player.getLocale();

		StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(map.size(), 9), locale.getFormattedMessage("tracks.track_panel", track.getName()));

		Material material = Material.of("STAINED_GLASS_PANE");

		String displayName = locale.getRawMessage("tracks.track_panel_entry_name");
		String attained = locale.getRawMessage("tracks.track_panel_entry_attained");
		String cost = locale.getRawMessage("tracks.track_panel_entry_cost");

		for (Group group : map.getGroups())
		{
			ItemStack item = material.toItem();

			ItemMeta meta = item.getMeta();

			meta.setDisplayName(Strings.format(displayName, group.getName()));

			List<String> lore = Lists.newArrayList(Strings.EMPTY);

			if (parents.contains(group))
			{
				lore.add(attained);

				item.setDurability(5);
			}
			else
			{
				item.setDurability(8);

				CurrencyAmount amount = map.getPrice(group);

				lore.add(Strings.format(cost, amount == null ? "FREE" : amount.toFormatter().wordFormat()));
			}

			meta.setAllLore(lore);

			item.setMeta(meta);

			
		}

		panel.open(player);
	}

}