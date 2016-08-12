package com.ulfric.core.gangs;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.core.teleport.Warp;
import com.ulfric.lib.coffee.string.Nameable;
import com.ulfric.lib.coffee.string.Unique;
import com.ulfric.lib.craft.location.Destination;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.location.LocationUtils;

public final class Gang implements Nameable, Unique {

	public static Gang fromDocument(Document document)
	{
		Validate.notNull(document);

		UUID uuid = UUID.fromString(document.getString("uuid"));
		String name = document.getString("name");

		Instant created = Instant.ofEpochMilli(document.getLong("created"));

		Warp home = null;
		String locationString = document.getString("home");
		if (locationString != null)
		{
			Location homeLocation = LocationUtils.fromString(locationString);
			if (homeLocation != null)
			{
				home = Warp.newWarp(name + "-home", Destination.newDestination(homeLocation, 5), null);
			}
		}

		Map<UUID, GangMember.Builder> members = Maps.newHashMap();
		Document membersDocument = document.getDocument("members");
		if (membersDocument != null)
		{
			for (String key : membersDocument.getKeys(false))
			{
				UUID memberUUID = UUID.fromString(key);

				Document memberDocument = membersDocument.getDocument(key);

				GangRank rank = GangRank.valueOf(memberDocument.getString("rank"));
				Instant joined = Instant.ofEpochMilli(memberDocument.getLong("joined"));

				members.put(memberUUID, GangMember.builder().setUUID(memberUUID).setRank(rank).setJoined(joined));
			}
		}

		Map<UUID, GangRelation> relations = Maps.newHashMap();
		Document relationsDocument = document.getDocument("relations");
		if (relationsDocument != null)
		{
			for (String key : relationsDocument.getKeys(false))
			{
				UUID gangUUID = UUID.fromString(key);

				Document relationDocument = relationsDocument.getDocument(key);

				Relation relation = Relation.valueOf(relationDocument.getString("relation"));
				Instant since = Instant.ofEpochMilli(relationDocument.getLong("since"));

				relations.put(gangUUID, new GangRelation(relation, since));
			}
		}

		Set<UUID> invites = document.getStringList("invites", ImmutableList.of()).stream().map(UUID::fromString).collect(Collectors.toSet());

		return new Gang(uuid, name, created, home, members, relations, invites);
	}

	Gang(UUID uuid, String name, Instant created, Warp home, Map<UUID, GangMember.Builder> members, Map<UUID, GangRelation> relations, Set<UUID> invites)
	{
		this.uuid = uuid;
		this.name = name;
		this.created = created;
		this.home = home;
		this.members = Maps.newHashMapWithExpectedSize(members.size());
		this.relations = relations;
		this.invites = invites;

		for (Entry<UUID, GangMember.Builder> entry : members.entrySet())
		{
			this.members.put(entry.getKey(), entry.getValue().setGang(this).build());
		}
	}

	private final UUID uuid;
	private final Instant created;

	private String oldName;
	private String name;
	private Warp home;
	private final Map<UUID, GangMember> members;
	private final Map<UUID, GangRelation> relations;
	private final Set<UUID> invites;

	@Override
	public UUID getUniqueId()
	{
		return this.uuid;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public void setName(String name)
	{
		this.oldName = this.name;
		this.name = name;

		this.save();
	}

	public String getOldName()
	{
		return this.oldName;
	}

	public Instant getCreated()
	{
		return this.created;
	}

	public GangMember getMember(UUID memberUUID)
	{
		return this.members.get(memberUUID);
	}

	public void addMember(GangMember member)
	{
		UUID memberUUID = member.getUniqueId();
		this.members.put(memberUUID, member);
		this.invites.remove(memberUUID);
		this.save();
	}

	public void removeMember(GangMember member)
	{
		this.removeMember(member.getUniqueId());
	}

	public void removeMember(UUID memberUUID)
	{
		this.members.put(memberUUID, null);
		this.save();
	}

	public boolean isInvited(UUID memberUUID)
	{
		return this.invites.contains(memberUUID);
	}

	public void addInvite(UUID memberUUID)
	{
		if (!this.invites.add(memberUUID)) return;

		this.save();
	}

	public void setHome(Location location)
	{
		if (location == null)
		{
			if (this.home == null) return;

			this.home = null;
		}
		else
		{
			this.home = Warp.newWarp(this.name + "-home", Destination.newDestination(location, 5), null);
		}

		this.save();
	}

	public Warp getHome()
	{
		return this.home;
	}

	public List<UUID> getMemberParticipants()
	{
		return ImmutableList.copyOf(this.members.keySet());
	}

	public List<UUID> getRelationParticipants()
	{
		return ImmutableList.copyOf(this.relations.keySet());
	}

	public List<GangMember> getMembers()
	{
		return ImmutableList.copyOf(this.members.values());
	}

	public void clearRelation(UUID gangUUID)
	{
		this.relations.put(gangUUID, null);

		this.save();
	}

	private void save()
	{
		Gangs.getInstance().updateGang(this);
	}

	public void into(MutableDocument document)
	{
		document.set("name", this.name);
		document.set("created", this.created.toEpochMilli());
		document.set("home", this.home == null ? null : this.home.locationToString());

		MutableDocument membersDocument = document.createDocument("members");
		for (Entry<UUID, GangMember> entry : this.members.entrySet())
		{
			String uuidString = entry.getKey().toString();
			GangMember member = entry.getValue();

			if (member == null)
			{
				membersDocument.set(uuidString, null);

				continue;
			}

			MutableDocument memberDocument = membersDocument.createDocument(uuidString);

			memberDocument.set("rank", member.getRank());
			memberDocument.set("joined", member.getJoined().toEpochMilli());
		}

		MutableDocument relationsDocument = document.createDocument("relations");
		for (Entry<UUID, GangRelation> entry : this.relations.entrySet())
		{
			UUID key = entry.getKey();
			String uuidString = key.toString();
			GangRelation relation = entry.getValue();

			if (relation == null || Gangs.getInstance().getGang(key) == null)
			{
				relationsDocument.set(uuidString, null);

				continue;
			}

			MutableDocument relationDocument = relationsDocument.createDocument(uuidString);

			relationDocument.set("relation", relation.getRelation());
			relationDocument.set("since", relation.getSince().toEpochMilli());
		}

		document.set("invites", this.invites.stream().map(Object::toString).collect(Collectors.toList()));
	}

}