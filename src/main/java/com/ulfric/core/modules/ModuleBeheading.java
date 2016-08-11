package com.ulfric.core.modules;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

import com.ulfric.lib.coffee.command.ArgFunction;
import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.math.RandomUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerKillPlayerEvent;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.inventory.item.Material;
import com.ulfric.lib.craft.inventory.item.WeaponType;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import com.ulfric.lib.craft.inventory.item.meta.SkullMeta;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.note.PlayableSound;
import com.ulfric.lib.craft.note.Sound;
import com.ulfric.lib.craft.string.ChatUtils;
import com.ulfric.lib.craft.world.World;

public class ModuleBeheading extends Module {

	public ModuleBeheading()
	{
		super("beheading", "Off with his head!", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandPlayerSkull());

		this.addListener(new Listener(this)
		{
			@Handler
			public void onPvPKill(PlayerKillPlayerEvent event)
			{
				Player killer = event.getKiller();
				WeaponType weapon = killer.getMainHand().getType().getWeaponType();

				double chance = 0;
				PlayableSound sound = null;

				if (weapon == WeaponType.SWORD)
				{
					chance = 0.23;
				}
				else if (weapon == WeaponType.AXE)
				{
					chance = 0.34;
					sound = PlayableSound.builder().setSound(Sound.of("ANVIL_BREAK")).setVolume(10).setPitch(5).build();
				}
				else if (weapon == WeaponType.BOW)
				{
					chance = 0.10;
					sound = PlayableSound.builder().setSound(Sound.of("BAT_TAKEOFF")).setVolume(10).setPitch(5).build();
				}
				else if (weapon == WeaponType.FIST)
				{
					chance = 0.01;
				}
				else return;

				if (!RandomUtils.percentChance(chance)) return;

				Player killed = event.getPlayer();
				Location headLocation = killed.getEyeLocation();
				World world = headLocation.getWorld();

				if (sound != null)
				{
					world.playSound(headLocation, sound);
				}

				world.dropItem(headLocation, ModuleBeheading.this.newSkull(killed.getName(), killer.getName()));
			}
		});
	}

	private final class CommandPlayerSkull extends Command
	{
		CommandPlayerSkull()
		{
			super("playerskull", ModuleBeheading.this, "skull");

			this.addArgument(Argument.builder().setPath("owner").addResolver(ArgFunction.STRING_FUNCTION).setUsage("playerskull.specify_owner").build());
			this.addEnforcer(Player.class::isInstance, "playerskull.must_be_player");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();
			String owner = (String) this.getObject("owner");

			player.sendLocalizedMessage("playerskull.spawned", owner);
			player.getInventory().addItem(ModuleBeheading.this.newSkull(owner, null));
		}
	}

	ItemStack newSkull(String owner, String killer)
	{
		ItemStack item = Material.of("SKULL").toItem(1, (short) 3);

		ItemMeta meta = item.getMeta();

		Validate.isInstanceOf(SkullMeta.class, meta);

		SkullMeta skull = (SkullMeta) meta;

		skull.setOwner(owner);

		skull.setDisplayName(owner + " 's Head");

		if (killer != null)
		{
			skull.setPluginLore(Arrays.asList(ChatUtils.color("&cKilled by " + killer)));
		}

		item.setMeta(skull);

		return item;
	}

}