package com.ulfric.core.modules;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.ulfric.config.ConfigFile;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.EntityType;
import com.ulfric.lib.craft.event.entity.EntitySpawnEvent;

public class ModuleEntityDisabler extends Module {

	public ModuleEntityDisabler()
	{
		super("entity-disabler", "Disable specific entity spawning", "1.0.0", "Packet");
	}

	Set<EntityType> blocked;

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener(this)
		{
			@Handler(ignoreCancelled = true)
			public void onSpawn(EntitySpawnEvent event)
			{
				if (!ModuleEntityDisabler.this.blocked.contains(event.getEntity().getType())) return;

				event.setCancelled(true);
			}
		});
	}

	@Override
	public void onModuleEnable()
	{
		ConfigFile config = this.getModuleConfig();

		this.blocked = config.getRoot().getStringList("types", ImmutableList.of("WITHER")).stream().map(EntityType::of).filter(Objects::nonNull).collect(Collectors.toSet());

		config.save();
	}

}