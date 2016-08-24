package com.ulfric.core.rankup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.ScopeListener;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.economy.Bank;
import com.ulfric.lib.coffee.economy.BankAccount;
import com.ulfric.lib.coffee.economy.CurrencyAmount;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.permission.Group;
import com.ulfric.lib.coffee.permission.PermissionsManager;
import com.ulfric.lib.coffee.permission.Track;
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

public enum Rankups implements ScopeListener<UUID> {

	INSTANCE;

	private final Map<Track, SortedMap<Group, CurrencyAmount>> rankups = Maps.newHashMap();
	private final Map<UUID, Track> trackCache = Maps.newHashMap();
	final Map<Track, MaterialData> trackItems = Maps.newHashMap();
	MultiSubscription<UUID, String> subscription;
	Track defaultTrack;

	public Rankup getActive(Player player)
	{
		Set<Group> groups = player.getParents();

		Track cached = this.trackCache.get(player.getUniqueId());

		if (cached != null)
		{
			if (SetUtils.isEmpty(groups))
			{
				Group first = cached.getNext(null);

				return this.newRankup(cached, first, cached.getNext(first));
			}

			for (Group group : groups)
			{
				Group next = cached.getNext(group);

				if (next == null) continue;

				return this.newRankup(cached, group, next);
			}
		}

		if (SetUtils.isEmpty(groups)) return null;

		Set<Track> tracks = player.getTracks();

		if (SetUtils.isEmpty(tracks)) return null;

		for (Track track : tracks)
		{
			for (Group group : groups)
			{
				Group next = track.getNext(group);

				if (next == null) continue;

				return this.newRankup(track, group, next);
			}
		}

		return null;
	}

	private Rankup newRankup(Track track, Group oldGroup, Group newGroup)
	{
		Map<Group, CurrencyAmount> costs = this.rankups.get(track);
		return new Rankup(track, oldGroup, newGroup, costs == null ? null : costs.get(newGroup));
	}

	void registerRankup(Group group, CurrencyAmount amount)
	{
		for (Track track : PermissionsManager.get().getTracksForGroup(group))
		{
			SortedMap<Group, CurrencyAmount> amounts = this.rankups.get(track);

			if (amounts == null)
			{
				amounts = Maps.newTreeMap();

				this.rankups.put(track, amounts);
			}

			amounts.put(group, amount);
		}
	}

	void clear()
	{
		this.rankups.clear();
		this.trackCache.clear();
		this.trackItems.clear();
	}

	@Override
	public void onAddition(UUID uuid)
	{
		String track = this.subscription.get(uuid).getValue();

		if (track == null)
		{
			this.trackCache.put(uuid, this.defaultTrack);

			return;
		}

		Track trackValue = PermissionsManager.get().getTrack(track);

		this.trackCache.put(uuid, trackValue == null ? this.defaultTrack : trackValue);
	}

	@Override
	public void onRemove(UUID uuid)
	{
		this.trackCache.remove(uuid);
	}

	public void setTrack(UUID uuid, Track track)
	{
		Validate.notNull(uuid);
		Validate.notNull(track);

		this.trackCache.put(uuid, track);

		this.subscription.get(uuid).setValue(track.getName());
	}

	public void openPanel(Player player)
	{
		Locale locale = player.getLocale();
		StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(this.trackItems.size(), 9), locale.getRawMessage("tracks.panel"));

		int index = 0;

		Track currentTrack = this.trackCache.get(player.getUniqueId());

		String title = locale.getRawMessage("track.panel_item_title");
		String rightClick = locale.getRawMessage("track.panel_right_click");
		String leftClick = locale.getRawMessage("track.panel_left_click");
		String completeMessage = locale.getRawMessage("track.complete");

		Set<Group> parents = player.getRecursiveParents();

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
						SortedMap<Group, CurrencyAmount> trackPrices = this.rankups.get(track);

						UUID uuid = clicked.getUniqueId();

						Group first = trackPrices.firstKey();
						if (!parents.contains(first))
						{
							CurrencyAmount amount = trackPrices.get(first);

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

							clicked.addParent(first);
						}
						// TODO charge player to join the track

						this.setTrack(uuid, track);

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

	public void openTrackPanel(Track track, Player player, Set<Group> parents)
	{
		Map<Group, CurrencyAmount> map = this.rankups.get(track);

		if (MapUtils.isEmpty(map)) return;

		Locale locale = player.getLocale();

		StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(map.size(), 9), locale.getFormattedMessage("tracks.track_panel", track.getName()));

		Material material = Material.of("STAINED_GLASS_PANE");

		String displayName = locale.getRawMessage("tracks.track_panel_entry_name");
		String attained = locale.getRawMessage("tracks.track_panel_entry_attained");
		String cost = locale.getRawMessage("tracks.track_panel_entry_cost");

		for (Entry<Group, CurrencyAmount> entry : map.entrySet())
		{
			Group group = entry.getKey();

			ItemStack item = material.toItem();

			ItemMeta meta = item.getMeta();

			meta.setDisplayName(Strings.format(displayName, group.getDisplayName()));

			List<String> lore = Lists.newArrayList(Strings.EMPTY);

			if (parents.contains(group))
			{
				lore.add(attained);

				item.setDurability(5);
			}
			else
			{
				item.setDurability(8);

				CurrencyAmount amount = entry.getValue();

				lore.add(Strings.format(cost, amount == null ? "FREE" : amount.toFormatter().wordFormat()));
			}

			meta.setAllLore(lore);

			item.setMeta(meta);

			
		}

		panel.open(player);
	}

}