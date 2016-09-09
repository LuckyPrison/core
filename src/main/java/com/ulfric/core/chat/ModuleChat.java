package com.ulfric.core.chat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.config.Document;
import com.ulfric.lib.coffee.collection.ListUtils;
import com.ulfric.lib.coffee.collection.SetUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.coffee.npermission.Group;
import com.ulfric.lib.coffee.npermission.Permissions;
import com.ulfric.lib.coffee.npermission.Track;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.AsyncPlayerChatEvent;
import com.ulfric.lib.craft.event.player.AsyncPlayerFormattedChatEvent;
import com.ulfric.lib.craft.string.ChatUtils;

import googletranslate.com.google.api.GoogleAPI;
import googletranslate.com.google.api.GoogleAPIException;
import googletranslate.com.google.api.translate.Language;
import googletranslate.com.google.api.translate.Translate;

public final class ModuleChat extends Module {

	public ModuleChat()
	{
		super("chat", "Responsible for all other chat-related things", "1.0.0", "Packet");
	}

	Map<Group, String> formats;
	Map<String, Map<String, String>> cachedTranslations;

	@Override
	public void onFirstEnable()
	{
		this.formats = Maps.newHashMap();
		this.cachedTranslations = Maps.newConcurrentMap();

		this.addModule(new ModuleChatToggler());
		this.addModule(ModuleNicknames.INSTANCE);

		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onChat(AsyncPlayerChatEvent event)
			{
				Set<Player> vanillaRecipients = event.getRecipients();

				Collection<Group> groups = event.getPlayer().getParents();

				Player player = event.getPlayer();
				final String message = event.getMessage();
				Map<String, Set<Player>> recipients = Maps.newHashMap();

				for (Player vanillaRecipient : vanillaRecipients)
				{
					recipients.computeIfAbsent(vanillaRecipient.getLocale().getLanguage(), k -> new HashSet<>()).add(vanillaRecipient);
				}

				AsyncPlayerFormattedChatEvent call = new AsyncPlayerFormattedChatEvent(player, message, recipients);

				String format = null;

				String groupFormat = "";

				if (!CollectionUtils.isEmpty(groups))
				{
					Track premium = Permissions.getTrack("premium");
					Track mine = Permissions.getTrack("mines");

					StringBuilder builder = new StringBuilder();

					Iterator<Group> mineIterator = ListUtils.reverseIterator(mine.getGroups());
					while (mineIterator.hasNext())
					{
						Group group = mineIterator.next();
						if (!groups.contains(group)) continue;

						builder.append('[');
						builder.append(group.getName());
						builder.append(']');

						break;
					}

					if (premium != null)
					{
						Iterator<Group> premiumIterator = ListUtils.reverseIterator(premium.getGroups());
						while (premiumIterator.hasNext())
						{
							Group group = premiumIterator.next();
							if (!groups.contains(group)) continue;

							builder.append(ChatUtils.color("&6"));
							builder.append(" [");
							builder.append(group.getName());
							builder.append(']');

							break;
						}
					}

					groupFormat = builder.toString();

					for (Group group : groups)
					{
						String found = ModuleChat.this.formats.get(group);

						if (found == null) continue;

						format = found;

						break;
					}
				}

				if (format == null)
				{
					format = ModuleChat.this.formats.get(null);

					if (format == null)
					{
						return;
					}
				}

				format = format.replace("{group}", groupFormat);

				// TODO tags
				format = format.replace("{tag}", "<TODO tags>");

				event.setCancelled(true);

				for (Player recipient : vanillaRecipients)
				{
					String language = recipient.getLocale().getLanguage();

					Set<Player> localizedRecipients = recipients.get(language);

					if (localizedRecipients == null)
					{
						localizedRecipients = Sets.newHashSet();

						recipients.put(language, localizedRecipients);
					}

					localizedRecipients.add(recipient);
				}

				call.fire();

				Set<Map.Entry<String, Set<Player>>> entries = recipients.entrySet();

				String senderLoc = player.getLocale().getLanguage();
				Set<Player> sameLangRecip = recipients.remove(senderLoc);

				String slFormat = format.replace("{message}", message);
				for (Player recipient : sameLangRecip)
				{
					recipient.sendMessage(slFormat.replace("{player}", ModuleNicknames.getName(player, recipient)));
				}

				this.log(Strings.format("{0} [{1}]: {2}", ChatUtils.stripColor(groupFormat), player.getName(), message));

				if (entries.isEmpty()) return;

				String lowerMessage = message.toLowerCase();

				Language senderLanguage = Language.fromString(senderLoc);

				assert senderLanguage != null;

				for (Map.Entry<String, Set<Player>> entry : entries)
				{
					String languageCode = entry.getKey();
					Set<Player> toSend = entry.getValue();

					Language language = Language.fromString(languageCode);

					if (language == null || language == senderLanguage)
					{
						String formatted = format.replace("{message}", message);

						for (Player recipient : toSend)
						{
							recipient.sendMessage(formatted.replace("{player}", ModuleNicknames.getName(player, recipient)));
						}

						continue;
					}

					Map<String, String> cache = ModuleChat.this.cachedTranslations.get(languageCode);

					if (cache == null)
					{
						cache = Collections.synchronizedMap(new CaseInsensitiveMap<>());

						ModuleChat.this.cachedTranslations.put(languageCode, cache);
					}
					else
					{
						String cached = cache.get(lowerMessage);

						if (cached != null)
						{
							String formatted = format.replace("{message}", cached);

							for (Player recipient : toSend)
							{
								recipient.sendMessage(formatted.replace("{player}", ModuleNicknames.getName(player, recipient)));
							}

							continue;
						}
					}

					try
					{
						String translated = Translate.DEFAULT.execute(message, senderLanguage, language);

						cache.put(lowerMessage, translated);

						String formatted = format.replace("{message}", translated);

						for (Player recipient : toSend)
						{
							recipient.sendMessage(formatted.replace("{player}", ModuleNicknames.getName(player, recipient)));
						}
					}
					catch (GoogleAPIException exception)
					{
						//exception.printStackTrace();

						String formatted = format.replace("{message}", message);

						for (Player recipient : toSend)
						{
							// TODO nicknames
							recipient.sendMessage(formatted.replace("{player}", ModuleNicknames.getName(player, recipient)));
						}
					}
				}
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		Document root = this.getModuleConfig().getRoot();

		Document translateDoc = root.getDocument("translate");

		if (translateDoc == null)
		{
			this.log("Could not find document: translate");
		}
		else
		{
			GoogleAPI.setHttpReferrer(translateDoc.getString("http-referrer"));
			GoogleAPI.setKey(translateDoc.getString("secret-key"));
		}

		Document formatsDoc = root.getDocument("formats");

		if (formatsDoc == null)
		{
			this.log("Could not find document: formats");

			return;
		}

		Set<String> keys = formatsDoc.getKeys(false);

		if (SetUtils.isEmpty(keys))
		{
			this.log("No formats found");

			return;
		}

		int count = 0;

		for (String key : keys)
		{
			Document formatDoc = formatsDoc.getDocument(key);

			if (formatDoc == null)
			{
				this.log("Could not load format: " + key);

				continue;
			}

			String groupString = formatDoc.getString("group", key);
			Group group = Permissions.getGroup(groupString);

			if (group == null && !groupString.toLowerCase().equals("default"))
			{
				this.log("Could not find group: " + groupString);

				continue;
			}

			String format = formatDoc.getString("format");

			if (format == null)
			{
				this.log("Missing format: " + key);

				continue;
			}

			this.formats.put(group, ChatUtils.deserialize(format));

			count++;
		}

		if (count == 0)
		{
			this.log("Could not load any formats");

			return;
		}

		if (count == 1)
		{
			this.log("Loaded 1 format");

			return;
		}

		this.log("Loaded " + count + " formats");
	}

	@Override
	public void onModuleDisable()
	{
		this.formats.clear();
	}

}