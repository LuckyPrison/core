package com.ulfric.core.settings;

import java.util.List;
import java.util.UUID;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.numbers.NumberUtils;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.panel.Button;
import com.ulfric.lib.craft.panel.Panel;
import com.ulfric.lib.craft.panel.standard.StandardPanel;

class CommandSettings extends Command {

	public CommandSettings(ModuleBase owner)
	{
		super("settings", owner);

		this.addEnforcer(Enforcers.IS_PLAYER, "settings.must_be_player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		List<Setting> settings = Settings.INSTANCE.getSettings();

		if (settings.isEmpty())
		{
			player.sendLocalizedMessage("settings.no_settings");

			return;
		}

		Locale locale = player.getLocale();

		StandardPanel panel = Panel.createStandard(NumberUtils.roundUp(settings.size(), 9), locale.getRawMessage("settings.panel_header"));

		UUID uuid = player.getUniqueId();

		int slot = 0;

		for (Setting setting : settings)
		{
			ItemStack item = this.getItem(locale, uuid, setting);

			panel.addButton(Button.builder().addSlot(slot++, item).addAction(event ->
			{
				setting.setData(uuid, setting.getNextData(setting.getData(uuid)));

				event.getClicked().setItem(event.getSlot(), this.getItem(locale, uuid, setting));
			}).build());
		}

		panel.open(player);
	}

	private ItemStack getItem(Locale locale, UUID uuid, Setting setting)
	{
		State state = setting.getState(uuid);

		ItemStack item = setting.getItem();
		ItemStack stateItem = state.getItem();

		if (stateItem != null)
		{
			item.setType(stateItem.getType());
			item.setDurability(stateItem.getDurability());
			item.setAmount(stateItem.getAmount());
		}

		ItemMeta meta = item.getMeta();

		meta.setDisplayName(locale.getRawMessage(meta.getDisplayName()));

		List<String> lore = meta.getAllLore();
		int size = lore.size();

		String text = locale.getRawMessage(state.getText());

		for (int x = 0; x < size; x++)
		{
			lore.set(x, Strings.format(lore.get(x), text));
		}

		meta.setAllLore(lore);

		item.setMeta(meta);

		return item;
	}

}