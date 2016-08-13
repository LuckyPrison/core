package com.ulfric.core.modules;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.ulfric.lib.coffee.command.Argument;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.Enforcers;
import com.ulfric.lib.coffee.command.Resolvers;
import com.ulfric.lib.coffee.concurrent.ThreadUtils;
import com.ulfric.lib.coffee.email.EmailUtils;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;

public class ModuleEmailInterface extends Module {

	public ModuleEmailInterface()
	{
		super("email-interface", "/email", "1.0.0", "Packet");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand(new CommandEmail());
		this.addCommand(new CommandConfirm());
	}

	private class CommandEmail extends Command
	{
		public CommandEmail()
		{
			super("email", ModuleEmailInterface.this);

			this.addArgument(Argument.builder().setPath("address").addSimpleResolver(EmailUtils::parseAddress).setUsage("email.specify_address").build());

			this.addEnforcer(Enforcers.IS_PLAYER, "email.must_be_player");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();

			String pending = player.getMetadataAsString("pending_email");

			if (pending != null)
			{
				player.sendLocalizedMessage("email.pending", pending);

				return;
			}

			InternetAddress address = (InternetAddress) this.getObject("address");

			String addy = address.getAddress();

			player.setMetadata("pending_email", addy);
			String code = RandomStringUtils.randomAlphabetic(6);
			player.setMetadata("pending_email_code", code);

			player.sendLocalizedMessage("email.verify", addy);

			ThreadUtils.runAsync(() -> player.sendEmail(player.getLocalizedMessage("email.confirmation_subject"), player.getLocalizedMessage("email.confirmation_subject", code)));
		}
	}

	private class CommandConfirm extends Command
	{
		public CommandConfirm()
		{
			super("confirm", ModuleEmailInterface.this);

			this.addArgument(Argument.builder().setPath("code").addResolver(Resolvers.STRING).setUsage("confirm.code_required").build());

			this.addEnforcer(Enforcers.IS_PLAYER, "confirm.must_be_player");
		}

		@Override
		public void run()
		{
			Player player = (Player) this.getSender();
			String entered = (String) this.getObject("code");

			String code = player.getMetadataAsString("pending_email_code");

			if (code == null)
			{
				player.sendLocalizedMessage("confirm.no_code_found");

				return;
			}

			int distance = StringUtils.getLevenshteinDistance(code.toLowerCase(), entered.toLowerCase(), 2);

			if (distance == -1)
			{
				player.sendLocalizedMessage("confirm.incorrect");

				return;
			}

			player.sendLocalizedMessage("confirm.correct");

			player.removeMetadata("pending_email_code");

			EmailUtils.setEmailAddress(player.getUniqueId(), String.valueOf(player.getMetadata("pending_email", true)));

			ThreadUtils.runAsync(() -> player.sendEmail(player.getLocalizedMessage("email.confirmed_subject"), player.getLocalizedMessage("email.confirmed")));
		}
	}

}