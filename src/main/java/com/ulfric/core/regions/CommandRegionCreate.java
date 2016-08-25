package com.ulfric.core.regions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.region.Region;
import com.ulfric.lib.coffee.region.Selection;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.region.RegionColl;

final class CommandRegionCreate extends Command {

	public CommandRegionCreate(ModuleBase owner)
	{
		super("create", owner);

		this.addOptionalArgument(Argument.builder().setPath("weight").addResolver(Resolvers.INTEGER).build());
		this.addArgument(Argument.builder().setPath("name").addResolver(Resolvers.STRING).build());

		this.addEnforcer(Enforcers.IS_PLAYER, "regions.must_be_player");
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getSender();

		Selection selection = player.getSelection();

		if (selection == null) return; // TODO errors

		if (!selection.isComplete()) return; // TODO errors

		String name = (String) this.getObject("name");

		Region region = RegionColl.getRegionByName(name);

		if (region != null)
		{
			player.sendLocalizedMessage("regions.specify_unique_name", region.getName(), region.getWorld());

			return;
		}

		int weight = (int) this.getObject("weight", 0);

		region = Region.newRegion(name, player.getWorld().getName(), selection.toShape(), null, weight);

		player.sendLocalizedMessage("regions.region_created", region.getName(), region.getWeight());
	}

}