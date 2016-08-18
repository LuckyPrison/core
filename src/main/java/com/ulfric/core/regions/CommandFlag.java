package com.ulfric.core.regions;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandSender;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.module.ModuleBase;
import com.ulfric.lib.coffee.region.Flag;
import com.ulfric.lib.coffee.region.FlagData;
import com.ulfric.lib.coffee.region.Flags;
import com.ulfric.lib.coffee.region.Region;
import com.ulfric.lib.craft.region.RegionColl;

final class CommandFlag extends Command {

	public CommandFlag(ModuleBase owner)
	{
		super("flag", owner);

		this.addArgument(Argument.builder().setPath("region").addSimpleResolver(RegionColl::getRegionByName).build());
		this.addArgument(Argument.builder().setPath("flag").addSimpleResolver(Flags.INSTANCE::getFlag).build());
		this.addArgument(Argument.builder().setPath("data").addResolver(Resolvers.STRING).build());
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		Region region = (Region) this.getObject("region");
		Flag flag = (Flag) this.getObject("flag");
		String data = (String) this.getObject("data");

		FlagData flagData = region.newFlag(flag);

		if (!flagData.setData(data))
		{
			sender.sendMessage("could not set flag data");

			return;
		}

		region.save();

		sender.sendMessage("new data: " + flagData.getDataAsString());
	}

}