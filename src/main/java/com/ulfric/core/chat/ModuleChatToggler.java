package com.ulfric.core.chat;

import java.util.Iterator;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.core.settings.Setting;
import com.ulfric.core.settings.Settings;
import com.ulfric.core.settings.State;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.event.Priority;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.AsyncPlayerChatEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;

class ModuleChatToggler extends Module {

	public ModuleChatToggler()
	{
		super("chat-toggler", "Allows enabling/disabling chat", "1.0.0", "Packet");
	}

	Setting setting;
	State enabled;
	State disabled;

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true, priority = Priority.LOWEST)
			public void onChat(AsyncPlayerChatEvent event)
			{
				Player player = event.getPlayer();

				if (ModuleChatToggler.this.setting.getState(player.getUniqueId()) == ModuleChatToggler.this.disabled)
				{
					player.sendLocalizedMessage("chat.must_enable_chat");

					event.setCancelled(true);

					return;
				}

				Iterator<Player> iterator = event.getRecipients().iterator();

				while (iterator.hasNext())
				{
					Player next = iterator.next();

					if (ModuleChatToggler.this.setting.getState(next.getUniqueId()) == ModuleChatToggler.this.enabled) continue;

					iterator.remove();
				}
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = this.getModuleConfig();
		MutableDocument root = config.getRoot();

		Document settings = root.getDocument("setting");

		if (settings == null)
		{
			settings = root.createDocument("setting");
		}

		String name = settings.getString("name", "chat");
		String itemName = settings.getString("item-name", "chat.setting_name");
		String description = settings.getString("description", "chat.setting_description");
		ItemStack item = Material.of(settings.getString("itemstack", "STAINED_GLASS_PANE")).toItem();
		ItemMeta meta = item.getMeta();
		meta.setDisplayName(itemName);
		item.setMeta(meta);
		int priority = settings.getInteger("priority", 0);

		Setting.Builder builder = Setting.builder().setName(name).setDescription(description).setItem(item).setPriority(priority);

		this.enabled = State.builder().setText(settings.getString("enabled-state-text", "chat.setting_state_enabled")).build();
		this.disabled = State.builder().setText(settings.getString("disabled-state-text", "chat.setting_state_disabled")).build();

		builder.addState(this.enabled);
		builder.addState(this.disabled);

		this.setting = builder.build();

		config.save();

		Settings.INSTANCE.addSetting(this.setting);
	}

	@Override
	public void onModuleDisable()
	{
		Settings.INSTANCE.removeSetting(this.setting);
	}

}