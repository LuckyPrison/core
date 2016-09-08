package com.ulfric.core.modules;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.Fireball;
import com.ulfric.lib.craft.entity.player.Player;

public final class ModuleFireball extends Module {

	public ModuleFireball()
	{
		super("fireball", "Fireball command", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandFireball());
	}

	private final class CommandFireball extends Command {

		CommandFireball()
		{
			super("fireball", ModuleFireball.this, "fb");

			this.addEnforcer(Enforcers.IS_PLAYER, "fireball-is-not-player");

			this.addPermission("fireball.use");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();

			Fireball fireball = player.launchProjectile(Fireball.class);

			fireball.setDirection(fireball.getDirection().multiply(2));

			player.sendLocalizedMessage("fireball-shot");
		}

	}

}
