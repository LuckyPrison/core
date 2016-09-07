package com.ulfric.core.minebuddy;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.ulfric.lib.coffee.module.Module;

public final class ModuleMinebuddy extends Module {

	public static final ModuleMinebuddy INSTANCE = new ModuleMinebuddy();

	private ModuleMinebuddy()
	{
		super("minebuddy", "Minebuddies!", "1.0.0", "Packet");
	}

	private Map<UUID, Minebuddy> buddies;
	private Map<UUID, Request> invites;

	public Minebuddy getBuddy(UUID uuid)
	{
		return this.buddies.get(uuid);
	}

	public void clear(Minebuddy buddy)
	{
		this.buddies.remove(buddy.getPlayer1());
		this.buddies.remove(buddy.getPlayer2());
	}

	public Request getRequest(UUID uuid)
	{
		return this.invites.get(uuid);
	}

	public void clearInvite(UUID uuid)
	{
		this.invites.remove(uuid);
	}

	public void setBuddy(UUID uuid, Minebuddy buddy)
	{
		this.buddies.put(uuid, buddy);
	}

	public void setInvite(UUID uuid, Request request)
	{
		this.invites.put(uuid, request);
	}

	@Override
	public void onFirstEnable()
	{
		this.buddies = Maps.newHashMap();
		this.invites = Maps.newHashMap();
		this.addCommand(new CommandMinebuddy(this));
	}

}