package com.ulfric.core.control;

import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.object.HashUtils;
import com.ulfric.lib.coffee.string.NamedBase;
import com.ulfric.lib.coffee.string.Unique;

public class Punisher extends NamedBase implements Unique {

	public static final UUID AGENT_UUID = UUID.fromString("4b13a0fb-90be-44b7-b9e0-a460b04b4b68");
	public static final String AGENT_NAME = "Ministry of Love";
	public static final Punisher AGENT = new Punisher(Punisher.AGENT_NAME, Punisher.AGENT_UUID);
	public static final Punisher CONSOLE = new Punisher("CONSOLE", null);

	public static Punisher valueOf(CommandSender sender)
	{
		Validate.notNull(sender);

		UUID uuid = sender.getUniqueId();

		if (uuid == null) return Punisher.CONSOLE;

		if (uuid.equals(Punisher.AGENT_UUID)) return Punisher.AGENT;

		return Punishments.getInstance().getPunisher(uuid);
	}

	public static Punisher valueOf(String contextual)
	{
		if (contextual.equals(Punisher.AGENT_NAME)) return Punisher.AGENT;

		if (contextual.equals(Punisher.CONSOLE.getName())) return Punisher.CONSOLE;

		return PunishmentHolder.valueOf(contextual);
	}

	protected Punisher(String name, UUID uuid)
	{
		super(name);

		this.uuid = uuid;
	}

	private final UUID uuid;

	@Override
	public final UUID getUniqueId()
	{
		return this.uuid;
	}

	public String getName(CommandSender sender)
	{
		return this.getName();
	}

	@Override
	public String toString()
	{
		return this.uuid == null ? this.getName() : this.uuid.toString();
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == this) return true;

		if (!object.getClass().equals(Punisher.class)) return false;

		Punisher punisher = (Punisher) object;

		if (punisher.uuid == null) return this.uuid == null;

		if (this.uuid == null) return false;

		return this.uuid.equals(punisher.uuid);
	}

	@Override
	public int hashCode()
	{
		return HashUtils.hash(31, this.uuid);
	}

}