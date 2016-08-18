package com.ulfric.core.gangs;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;

public final class Gangs {

	private static final Gangs INSTANCE = new Gangs();

	public static Gangs getInstance()
	{
		return Gangs.INSTANCE;
	}

	private final Map<UUID, Gang> gangs = Maps.newHashMap();
	private final Map<String, Gang> gangsByName = new CaseInsensitiveMap<>();
	private final Map<UUID, GangMember> members = Maps.newHashMap();
	private MapSubscription<Document> subscription;

	void setSubscription(MapSubscription<Document> subscription)
	{
		this.subscription = subscription;
	}

	public Gang getGang(UUID uuid)
	{
		return this.gangs.get(uuid);
	}

	public Gang getGang(String gang)
	{
		return this.gangsByName.get(gang);
	}

	public Gang resolveGang(String context)
	{
		Gang gang = this.getGang(context);

		if (gang != null) return gang;

		UUID uuid = null;

		if (context.length() == 36 && context.contains("-"))
		{
			try
			{
				uuid = UUID.fromString(context);

				gang = this.getGang(uuid);

				if (gang != null) return gang;
			}
			catch (IllegalArgumentException exception) { }
		}

		Player player = null;

		if (uuid != null)
		{
			player = PlayerUtils.getPlayer(uuid);
		}

		if (player == null)
		{
			player = PlayerUtils.getPlayer(context);

			if (player == null) return null;
		}

		GangMember member = this.getMember(player.getUniqueId());

		if (member == null) return null;

		return member.getGang();
	}

	public GangMember getMember(UUID uuid)
	{
		return this.members.get(uuid);
	}

	void registerGang(Gang gang)
	{
		this.gangs.put(gang.getUniqueId(), gang);
		this.gangsByName.put(gang.getName(), gang);

		gang.getMembers().forEach(this::registerMember);
	}

	void registerMember(GangMember member)
	{
		this.members.put(member.getUniqueId(), member);
	}

	void updateGang(Gang gang)
	{
		this.gangsByName.put(gang.getName(), gang);
		this.gangsByName.remove(gang.getOldName());

		MutableDocument gangData = new SimpleDocument();
		gang.into(gangData);

		this.subscription.setField(gang.getUniqueId().toString(), gangData);
	}

	void deleteGang(Gang gang)
	{
		UUID uuid = gang.getUniqueId();
		this.gangs.remove(uuid);
		this.gangsByName.remove(gang.getName());

		for (UUID otherUUID : gang.getRelationParticipants())
		{
			Gang otherGang = this.gangs.get(otherUUID);

			if (otherGang == null) continue;

			otherGang.clearRelation(uuid);
		}

		for (Gang relation : this.gangs.values())
		{
			relation.clearRelation(uuid);
		}

		gang.getMemberParticipants().forEach(this.members::remove);

		this.subscription.removeField(gang.getUniqueId().toString());
	}

}