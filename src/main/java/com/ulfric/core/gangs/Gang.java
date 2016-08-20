package com.ulfric.core.gangs;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.config.Document;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.string.Nameable;
import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.coffee.string.Unique;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.entity.player.PlayerUtils;
import com.ulfric.lib.craft.location.Destination;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.location.LocationUtils;

public final class Gang implements Nameable, Unique, Comparable<Gang> {

	public static Gang fromDocument(Document document)
	{
		Validate.notNull(document);

		String uuidString = document.getString("uuid");

		if (uuidString == null) return null;

		UUID uuid = UUID.fromString(uuidString);

		if (uuid == null) return null;

		String name = document.getString("name");

		Instant created = Instant.ofEpochMilli(document.getLong("created"));

		Destination home = null;
		String locationString = document.getString("home");
		if (locationString != null)
		{
			Location homeLocation = LocationUtils.fromString(locationString);
			if (homeLocation != null)
			{
				home = Destination.newDestination(homeLocation, 5);
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

				GangRank rank = GangRank.valueOf(memberDocument.getString("rank", GangRank.MEMBER.name()));
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

	public static Gang newGang(Gangs gangs, UUID uuid, String name, UUID owner)
	{
		Validate.notNull(gangs);
		Validate.notNull(uuid);
		Validate.notNull(name);
		Validate.notNull(owner);

		Instant created = Instant.now();

		Destination home = null;

		Map<UUID, GangMember.Builder> members = Maps.newHashMap();
		members.put(owner, GangMember.builder().setUUID(owner).setJoined(created).setRank(GangRank.LEADER));

		Map<UUID, GangRelation> relations = Maps.newHashMap();

		Set<UUID> invites = Sets.newHashSet();

		Gang gang = new Gang(uuid, name, created, home, members, relations, invites);

		gangs.registerGang(gang);

		gang.save();

		return gang;
	}

	Gang(UUID uuid, String name, Instant created, Destination home, Map<UUID, GangMember.Builder> members, Map<UUID, GangRelation> relations, Set<UUID> invites)
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
	private Destination home;
	private final Map<UUID, GangMember> members;
	private final Map<UUID, GangRelation> relations;
	private final Set<UUID> invites;

	private static final long MILLIS_THRESHOLD = TimeUnit.MINUTES.toMillis(10);
	private int cachedCompareSize;
	private long lastCacheUpdate = System.currentTimeMillis();

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

	public List<GangMember> getMembersByRank(GangRank rank)
	{
		Validate.notNull(rank);

		List<GangMember> list = Lists.newArrayList();

		for (GangMember member : this.members.values())
		{
			if (member.getRank().compareTo(rank) < 0) continue;

			list.add(member);
		}

		return list;
	}

	public GangMember addNewMember(UUID memberUUID)
	{
		Validate.notNull(memberUUID);

		GangMember member = GangMember.builder()
									  .setUUID(memberUUID)
									  .setGang(this)
									  .setRank(GangRank.MEMBER)
									  .setJoined(Instant.now())
									  .build();

		this.addMember(member);

		return member;
	}

	public void addMember(GangMember member)
	{
		Validate.notNull(member);
		Validate.isTrue(this.equals(member.getGang()));

		UUID memberUUID = member.getUniqueId();
		this.members.put(memberUUID, member);
		this.invites.remove(memberUUID);
		this.save();
	}

	public void removeMember(GangMember member)
	{
		Validate.notNull(member);

		this.removeMember(member.getUniqueId());
	}

	public void removeMember(UUID memberUUID)
	{
		Validate.notNull(memberUUID);

		this.members.remove(memberUUID);
		this.save();
	}

	public void setRank(UUID memberUUID, GangRank rank)
	{
		Validate.notNull(memberUUID);
		Validate.notNull(rank);

		GangMember oldMember = this.members.get(memberUUID);

		if (oldMember == null) return;

		GangMember newMember = GangMember.builder().setGang(this).setJoined(oldMember.getJoined()).setRank(rank).build();

		Gangs.getInstance().registerMember(newMember);

		this.members.put(newMember.getUniqueId(), newMember);
	}

	public boolean isInvited(UUID memberUUID)
	{
		Validate.notNull(memberUUID);

		return this.invites.contains(memberUUID);
	}

	public void addInvite(UUID memberUUID)
	{
		Validate.notNull(memberUUID);

		if (!this.invites.add(memberUUID)) return;

		this.save();
	}

	public void removeInvite(UUID memberUUID)
	{
		Validate.notNull(memberUUID);

		if (!this.invites.remove(memberUUID)) return;

		this.save();
	}

	public List<UUID> getInvites()
	{
		return ImmutableList.copyOf(this.invites);
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
			this.home = Destination.newDestination(location, 5);
		}

		this.save();
	}

	public Destination getHome()
	{
		return this.home;
	}

	public List<UUID> getMemberParticipants()
	{
		return ImmutableList.copyOf(this.members.keySet());
	}

	public Set<Player> getOnlinePlayers()
	{
		return PlayerUtils.getPlayers(this.members.keySet());
	}

	public List<UUID> getRelationParticipants()
	{
		return ImmutableList.copyOf(this.relations.keySet());
	}

	public List<UUID> getRelations(Relation relation)
	{
		List<UUID> list = Lists.newArrayList();

		for (Entry<UUID, GangRelation> entry : this.relations.entrySet())
		{
			if (entry.getValue().getRelation() != relation) continue;

			list.add(entry.getKey());
		}

		return list;
	}

	public GangRelation getRelation(UUID gangUUID)
	{
		return this.relations.get(gangUUID);
	}

	public List<GangMember> getMembers()
	{
		return ImmutableList.copyOf(this.members.values());
	}

	public void clearRelation(UUID gangUUID)
	{
		this.relations.remove(gangUUID);

		this.save();
	}

	public void setRelation(UUID uuid, Relation relation)
	{
		GangRelation current = this.relations.get(uuid);

		if (current != null)
		{
			if (current.getRelation() == relation) return;
		}

		this.relations.put(uuid, new GangRelation(relation, Instant.now()));

		this.save();
	}

	private void save()
	{
		Gangs.getInstance().updateGang(this);
	}

	public void into(MutableDocument document)
	{
		document.set("uuid", this.uuid.toString());
		document.set("name", this.name);
		document.set("created", this.created.toEpochMilli());
		document.set("home", this.home == null ? null : this.home.locationToString());

		Set<Entry<UUID, GangMember>> memberSet = this.members.entrySet();
		if (!memberSet.isEmpty())
		{
			MutableDocument membersDocument = document.createDocument("members");
			for (Entry<UUID, GangMember> entry : memberSet)
			{
				String uuidString = entry.getKey().toString();
				GangMember member = entry.getValue();

				if (member == null)
				{
					membersDocument.set(uuidString, null);

					continue;
				}

				MutableDocument memberDocument = membersDocument.createDocument(uuidString);

				memberDocument.set("rank", member.getRank().name());
				memberDocument.set("joined", member.getJoined().toEpochMilli());
			}
		}

		Set<Entry<UUID, GangRelation>> relationSet = this.relations.entrySet();
		if (!relationSet.isEmpty())
		{
			MutableDocument relationsDocument = document.createDocument("relations");
			for (Entry<UUID, GangRelation> entry : relationSet)
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

				relationDocument.set("relation", relation.getRelation().name());
				relationDocument.set("since", relation.getSince().toEpochMilli());
			}
		}

		document.set("invites", this.invites.stream().map(Object::toString).collect(Collectors.toList()));
	}

	@Override
	public int hashCode()
	{
		return this.uuid.hashCode();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == this) return true;

		if (!(object instanceof Gang)) return false;

		return ((Gang) object).uuid.equals(this.uuid);
	}

	@Override
	public int compareTo(Gang gang)
	{
		int compare = Integer.compare(this.calculateCompareSize(), gang.calculateCompareSize());

		if (compare == 0)
		{
			compare = Integer.compare(this.members.size(), gang.members.size());

			if (compare == 0)
			{
				compare = this.name.compareToIgnoreCase(gang.name);

				if (compare == 0)
				{
					compare = this.created.compareTo(gang.created);
				}
			}
		}

		return compare;
	}

	private int calculateCompareSize()
	{
		final long current = System.currentTimeMillis();

		if (current - this.lastCacheUpdate <= Gang.MILLIS_THRESHOLD)
		{
			return this.cachedCompareSize;
		}

		this.lastCacheUpdate = current;

		return this.cachedCompareSize = this.getOnlinePlayers().size();
	}

	@Override
	public String toString()
	{
		return Strings.format("Gang[name={0}, uuid={1}]", this.name, this.uuid);
	}

}