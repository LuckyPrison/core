package com.ulfric.core.donorpart;

import com.ulfric.config.ConfigFile;
import com.ulfric.config.MutableDocument;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.event.Handler;
import com.ulfric.lib.coffee.event.Listener;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.event.player.PlayerJoinEvent;
import com.ulfric.lib.craft.location.Location;
import com.ulfric.lib.craft.world.Effect;

public class ModuleDonorParticle extends Module {

	private Effect effect;

	public ModuleDonorParticle()
	{
		super("donor-particle", "Donor join particle", "1.0.0", "insou");
	}

	@Override
	public void onFirstEnable()
	{
		ConfigFile config = super.getModuleConfig();
		MutableDocument document = config.getRoot();

		if (!document.contains("effect"))
		{
			document.set("effect", Effect.of("EXPLOSION_LARGE").getName());

			config.save();
		}

		this.effect = Effect.of(document.getString("effect"));

		this.addListener(new JoinListener());
	}

	private class JoinListener extends Listener {

		public JoinListener()
		{
			super(ModuleDonorParticle.this);
		}

		@Handler
		public void on(PlayerJoinEvent event)
		{
			Player player = event.getPlayer();

			if (!player.hasPermission("donor.particle"))
			{
				return;
			}

			ThreadUtils.runLater(() ->
			{
				Location location = player.getLocation();

				location.getWorld().playEffect(location, ModuleDonorParticle.this.effect, 0);
			}, 50L);
		}

	}

}
