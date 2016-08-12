package com.ulfric.core.gangs;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.config.SimpleDocument;
import com.ulfric.data.MapSubscription;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;

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

	private void registerMember(GangMember member)
	{
		this.members.put(member.getUniqueId(), member);
	}

	void updateGang(Gang gang)
	{
		this.gangsByName.put(gang.getName(), gang);
		this.gangsByName.remove(gang.getOldName());

		MutableDocument wrapper = new SimpleDocument();
		gang.into(wrapper.createDocument(gang.getUniqueId().toString()));

		ThreadUtils.runAsync(() -> this.subscription.updateFields(wrapper));
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

		gang.getMemberParticipants().forEach(this.members::remove);

		ThreadUtils.runAsync(() -> this.subscription.removeField(gang.getUniqueId().toString()));
	}

}