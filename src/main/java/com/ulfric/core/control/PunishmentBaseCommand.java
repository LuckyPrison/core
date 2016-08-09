package com.ulfric.core.control;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.ModuleBase;

abstract class PunishmentBaseCommand extends Command {

	private static final Argument SILENT = Argument.builder().setPath("silent").addSimpleResolver(str -> (str.equals("--s") || str.equals("-silent")) ? true : null).build();

	public PunishmentBaseCommand(String name, ModuleBase owner)
	{
		super(name, owner);

		this.setupBase();
	}

	public PunishmentBaseCommand(String name, ModuleBase owner, String... dynamicAliases)
	{
		super(name, owner, dynamicAliases);

		this.setupBase();
	}

	private void setupBase()
	{
		this.addArgument(PunishmentHolder.ARGUMENT);
		this.addArgument(Punishment.REFERENCE_ARGUMENT);
		this.addOptionalArgument(PunishmentBaseCommand.SILENT);

		this.addPermission("control.base");
	}

	public final Punisher getPunisher()
	{
		return Punisher.valueOf(this.getSender());
	}

	public final PunishmentHolder getHolder()
	{
		return (PunishmentHolder) this.getObject(PunishmentHolder.ARGUMENT.getPath());
	}

	public final int[] getReferenced()
	{
		return (int[]) this.getObject(Punishment.REFERENCE_ARGUMENT.getPath());
	}

	public final boolean isSilent()
	{
		return this.hasObject(PunishmentBaseCommand.SILENT.getPath());
	}

}