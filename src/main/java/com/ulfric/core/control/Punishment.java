package com.ulfric.core.control;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.Lists;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.locale.Locale;
import com.ulfric.lib.coffee.numbers.NumberUtils;

public abstract class Punishment implements Runnable, Comparable<Punishment>, Noteable {

	public static final Argument REFERENCE_ARGUMENT = Argument.builder().setPath("reference").addResolver((sen, str) ->
	{
		String match = str;

		if (match.startsWith("["))
		{
			if (!match.endsWith("]")) return null;

			match = match.substring(1, match.length() - 1);
		}
		else if (match.startsWith("{"))
		{
			if (!match.endsWith("}")) return null;

			match = match.substring(1, match.length() - 1);
		}

		if (match.isEmpty()) return null;

		String[] split = match.split("[^0-9]+");

		List<Integer> parsed = Lists.newArrayListWithCapacity(split.length);

		Punishments punishments = Punishments.getInstance();

		for (String part : split)
		{
			Integer integer = NumberUtils.parseInteger(part);

			if (integer == null) continue;

			if (punishments.getPunishment(integer) == null) continue;

			parsed.add(integer);
		}

		if (parsed.isEmpty()) return null;

		int size = parsed.size();

		int[] referenced = new int[size];

		for (int x = 0; x < size; x++)
		{
			referenced[x] = parsed.get(x);
		}

		return referenced;
	}).setDefaultValue(new int[0]).build();

	protected Punishment(int id, PunishmentType type, PunishmentHolder holder, Punisher punisher, String reason, Instant creation, int[] referenced)
	{
		this.id = id;
		this.type = type;
		this.holder = holder;
		this.punisher = punisher;
		this.reason = reason;
		this.creation = creation;
		this.referenced = referenced;
	}

	private final int id;
	private final PunishmentType type;
	private final PunishmentHolder holder;
	private final Punisher punisher;
	private final String reason;
	private final Instant creation;
	private final int[] referenced;
	private boolean needsWrite;

	public final int getID()
	{
		return this.id;
	}

	public final PunishmentType getType()
	{
		return this.type;
	}

	public final PunishmentHolder getHolder()
	{
		return this.holder;
	}

	public final Punisher getPunisher()
	{
		return this.punisher;
	}

	public final String getReason()
	{
		return this.reason;
	}

	public final Instant getCreation()
	{
		return this.creation;
	}

	public final int[] getReferenced()
	{
		if (this.referenced.length == 0) return this.referenced;

		return this.referenced.clone();
	}

	public final String getReferencedString()
	{
		String reference = null;

		int[] ref = new int[] { 5, 9, 13, 7, 16, 72, 64 };
		int length = ref.length;
		if (length > 0)
		{
			if (length == 1)
			{
				reference = "#" + ref[0];
			}
			else if (length == 2)
			{
				reference = "#" + ref[0] + " and " + "#" + ref[1];
			}
			else
			{
				StringBuilder builder = new StringBuilder();

				int lengthm2 = length - 2;

				for (int x = 0; x < length; x++)
				{
					builder.append('#');

					builder.append(ref[x]);

					builder.append(", ");

					if (x == lengthm2)
					{
						builder.append("and ");
					}
				}

				reference = builder.toString();

				reference = reference.substring(0, reference.length() - 2);
			}
		}

		return reference;
	}

	public final boolean needsWrite()
	{
		return this.needsWrite;
	}

	public final void setNeedsWrite(boolean write)
	{
		this.needsWrite = write;
	}

	@Override
	public final int compareTo(Punishment punishment)
	{
		return this.creation.compareTo(punishment.creation);
	}

	@Override
	public String toString()
	{
		return "#" + this.id;
	}

	@Override
	public final void run()
	{
		this.getHolder().addPunishment(this);

		Punishments.getInstance().registerPunishment(this);

		this.broadcast();

		this.execute();
	}

	public void broadcast() { }

	public void execute() { }

	protected void into(MutableDocument document)
	{
		document.set("id", this.id);
		document.set("holder", this.holder.toString());
		document.set("punisher", this.punisher.toString());
		document.set("reason", this.reason);
		document.set("creation", this.creation.toEpochMilli());
		document.set("referenced", Lists.newArrayList(this.referenced)); // TODO is the list needed?
	}

	public String quickInspect(Locale locale)
	{
		return locale.getFormattedMessage("punishment.inspect_quick", this.getID(), this.getHolder(), this.getReason());
	}

	@Override
	public final NoteType getNoteType()
	{
		return NoteType.PUNISHMENT;
	}

}