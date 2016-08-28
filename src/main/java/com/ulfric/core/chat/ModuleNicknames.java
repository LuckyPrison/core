package com.ulfric.core.chat;

import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;

import com.ulfric.config.Document;
import com.ulfric.core.settings.Setting;
import com.ulfric.core.settings.Settings;
import com.ulfric.core.settings.State;
import com.ulfric.data.DataAddress;
import com.ulfric.data.MultiSubscription;
import com.ulfric.data.scope.PlayerScopes;
import com.ulfric.data.scope.ScopeListener;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.inventory.item.ItemParts;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.string.ChatUtils;

public final class ModuleNicknames extends Module implements ScopeListener<UUID> {

	static final ModuleNicknames INSTANCE = new ModuleNicknames();

	public static String getName(Player player, Player observer)
	{
		return ModuleNicknames.INSTANCE.getDisplayName(player, observer);
	}

	private ModuleNicknames()
	{
		super("nicknames", "Nicknames module", "1.0.0", "Packet");
	}

	private State enabled;
	private State disabled;
	//private State dunabled;
	private Setting setting;
	MultiSubscription<UUID, String> subscription;

	@Override
	public void onAddition(UUID uuid)
	{
		String nick = this.subscription.get(uuid).getValue();

		if (nick == null) return;

		Player player = PlayerUtils.getOnlinePlayer(uuid);

		player.setNickname(nick);
	}

	@Override
	public void onRemove(UUID uuid) { }

	public String getDisplayName(Player player, Player observer)
	{
		String nickname = player.getNickname();
		String name = player.getName();

		if (nickname == null || nickname == name) return name;

		State state = this.setting.getState(observer.getUniqueId());

		if (state == this.disabled)
		{
			return name;
		}

		return '~' +  nickname;
	}

	@Override
	public void onFirstEnable()
	{
		this.enabled = State.builder().setText("chat.setting_nicknames_enabled").build();
		this.disabled = State.builder().setText("chat.setting_nicknames_disabled").build();
		//this.dunabled = State.builder().setText("chat.setting_nicknames_dunabled").build();

		this.addCommand(new CommandNickname());

		this.subscription = PlayerUtils.getPlayerData().multi(String.class, PlayerScopes.ONLINE, new DataAddress<>("nicknames", null, "nickname")).blockOnSubscribe(true).subscribe();
	}

	@Override
	public void onModuleDisable()
	{
		Settings.INSTANCE.removeSetting(this.setting);
		PlayerScopes.ONLINE.removeListener(this);
		this.subscription.unsubscribe();
	}

	@Override
	public void onModuleEnable()
	{
		Document root = this.getModuleConfig().getRoot();

		Document settings = root.getDocument("setting");

		if (settings == null)
		{
			this.log("Unable to find document: setting");

			return;
		}

		String name = settings.getString("name", "nicknames");
		String itemName = "chat.setting_name_nickname";
		String description = "chat.setting_description_nickname";
		ItemStack item = ItemParts.stringToItem(settings.getString("itemstack", "id.NAME_TAG"));
		ItemMeta meta = item.getMeta();
		meta.setDisplayName(itemName);
		item.setMeta(meta);
		int priority = settings.getInteger("priority", 0);

		Setting.Builder builder = Setting.builder().setName(name).setDescription(description).setItem(item).setPriority(priority);

		builder.addState(this.enabled);
		builder.addState(this.disabled);
		//builder.addState(this.dunabled);

		this.setting = builder.build();

		Settings.INSTANCE.addSetting(this.setting);
		PlayerScopes.ONLINE.addListener(this);
		this.subscription.subscribe();
	}

	private class CommandNickname extends Command
	{
		public CommandNickname()
		{
			super("nickname", ModuleNicknames.this, "nick");

			this.addPermission("nickname.use");

			this.addEnforcer(Enforcers.IS_PLAYER, "nickname.must_be_player");

			this.addOptionalArgument(Argument.builder().setPath("off").addResolver(Resolvers.ignoreCase("off", "disable", "clear")).build());
			this.addOptionalArgument(Argument.builder().setPath("nick").addResolver(Resolvers.STRING).build());
		}

		private final Pattern pattern = Pattern.compile("[a-zA-Z0-9_{color_char}]+".replace("{color_char}", String.valueOf(ChatUtils.colorChar())));

		@Override
		public void run()
		{
			CommandSender sender = this.getSender();

			if (this.hasObject("off"))
			{
				// TODO handle disabling

				return;
			}

			Object nickObject = this.getObject("nick");

			if (!(nickObject instanceof String))
			{
				// TODO send nick

				return;
			}

			String nickStr = (String) nickObject;

			String nick = sender.hasPermission("nickname.colored") ? ChatUtils.color(nickStr) : nickStr;

			if (!sender.hasPermission("nickname.color.all"))
			{
				nick = ChatUtils.stripNaughtyColors(nick);

				nick = this.pattern.matcher(nick).replaceAll(Strings.EMPTY);

				if (nick.length() > 16)
				{
					if (ChatUtils.stripColor(nick).length() > 16)
					{
						nick = nick.substring(0, 15);

						String colorChar = String.valueOf(ChatUtils.colorChar());

						if (nick.endsWith(colorChar))
						{
							nick = nick.substring(0, nick.length()-2);

							// extra security
							Validate.isTrue(!nick.endsWith(colorChar));
						}
					}
				}
			}

			ModuleNicknames.this.subscription.get(sender.getUniqueId()).setValue(nick);

			sender.sendLocalizedMessage("nickname.set", nick);
		}
	}

}