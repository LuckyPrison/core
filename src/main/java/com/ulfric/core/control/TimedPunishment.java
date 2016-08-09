package com.ulfric.core.control;

import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.enums.EnumUtils;
import com.ulfric.lib.coffee.math.TimeUtils;

abstract class TimedPunishment extends Punishment {

	public static final Argument TIME_ARGUMENT = Argument.builder().setPath("time").addSimpleResolver(TimeUtils::future).setDefaultValue(Instant.MAX).build();

	protected TimedPunishment(int id, PunishmentType type, PunishmentHolder holder, Punisher punisher, String reason, Instant placed, Instant expiry, int[] referenced)
	{
		super(id, type, holder, punisher, reason, placed, referenced);

		this.expiry = expiry;
	}

	private Instant expiry;

	public final Instant getExpiry()
	{
		return this.expiry;
	}

	public final void setExpiry(Instant instant)
	{
		Instant oldValue = this.expiry;

		this.expiry = instant == null ? Instant.now() : instant;

		if (this.expiry.equals(oldValue)) return;

		this.setNeedsWrite(true);
	}

	public final boolean hasExpiry()
	{
		return this.expiry != Instant.MAX;
	}

	public final boolean isExpired()
	{
		return this.expiry.isBefore(Instant.now());
	}

	public final boolean isNotExpired()
	{
		return !this.isExpired();
	}

	public final String expiryToString()
	{
		Instant now = Instant.now();

		StringBuilder builder = new StringBuilder();

		Instant expires = this.getExpiry();
		builder.append(TimeUtils.formatMillis(TimeUtils.subtract(expires, now).toEpochMilli(), TimeUnit.MILLISECONDS));
		builder.append(" (");
		builder.append(EnumUtils.format(expires.atZone(ZoneId.systemDefault()).getDayOfWeek()));
		builder.append(')');

		return builder.toString();
	}

	@Override
	protected void into(MutableDocument document)
	{
		super.into(document);

		document.set("expiry", this.hasExpiry() ? this.expiry.toEpochMilli() : -1);
	}

}