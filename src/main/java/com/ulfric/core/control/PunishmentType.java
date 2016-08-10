package com.ulfric.core.control;

import java.util.function.Function;

import org.apache.commons.lang3.Validate;

import com.ulfric.config.Document;

public enum PunishmentType {

	BAN(Ban::fromDocument),
	KICK(Kick::fromDocument),
	MUTE(Mute::fromDocument),
	COMMAND_MUTE(CmdMute::fromDocument),
	SHADOW_MUTE(ShadowMute::fromDocument),
	WARN(Warn::fromDocument);

	private final Function<Document, Punishment> function;

	PunishmentType(Function<Document, Punishment> function)
	{
		Validate.notNull(function);

		this.function = function;
	}

	public Punishment fromDocument(Document document)
	{
		return this.function.apply(document);
	}

}