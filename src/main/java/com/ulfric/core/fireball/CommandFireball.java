package com.ulfric.core.fireball;

import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.craft.command.Enforcers;
import com.ulfric.lib.craft.entity.Fireball;
import com.ulfric.lib.craft.entity.player.Player;

final class CommandFireball extends Command {

	CommandFireball(ModuleFireball owner)
	{
		super("fireball", owner, "fb");

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
