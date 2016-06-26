package com.ulfric.core.mines;

import com.ulfric.core.mines.mixin.IMineSelector;
import com.ulfric.core.mines.mixin.MineSelectorImpl;
import com.ulfric.core.modules.ModuleMines;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.command.CommandKey;
import com.ulfric.lib.coffee.region.Cuboid;
import com.ulfric.lib.coffee.region.Region;
import com.ulfric.lib.coffee.tuple.Weighted;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.Material;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class CommandMines extends Command {

	private Mines mines;

	public CommandMines(ModuleMines owner)
	{
		super("mine", owner);
		this.mines = owner.getMines();
		this.addCommand(new CommandSelect(), CommandKey.builder().add("select").build());
		this.addCommand(new CommandCreate(), CommandKey.builder().add("create").build());
		this.addCommand(new CommandModify(), CommandKey.builder().add("modify").build());
		this.addCommand(new CommandRemove(), CommandKey.builder().add("remove").build());
		this.addCommand(new CommandMoveMine(), CommandKey.builder().add("move").add("movemine").build());
		this.addCommand(new CommandSetPermissions(), CommandKey.builder().add("setpermissions").build());
		this.addCommand(new CommandInfo(), CommandKey.builder().add("info").build());
	}

	@Override
	public void run()
	{
		getSender().sendLocalizedMessage("core.mines.info");
	}

	public Mines getMines()
	{
		return mines;
	}

	private class CommandAdmin extends Command {

		private CommandAdmin()
		{
			super("admin", CommandMines.this.getOwner());
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.admin")) return;
			if (getSender() instanceof Player)
			{
				Player player = (Player) getSender();
				if (player.getMixin(IMineSelector.class) != null)
				{
					player.uninstall(IMineSelector.class);
					player.sendLocalizedMessage("core.mines.admin.disabled");
				}
				else
				{
					player.install(IMineSelector.class, new MineSelectorImpl());
					player.sendLocalizedMessage("core.mines.admin.enabled");
				}
			}
		}
	}

	private class CommandSelect extends Command {

		private CommandSelect()
		{
			super("select", CommandMines.this.getOwner());
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.select_region")) return;
			if (getSender() instanceof Player)
			{
				Player player = (Player) getSender();
				if (player.getMixin(IMineSelector.class) != null)
				{
					IMineSelector selector = player.as(IMineSelector.class);

					Validate.notNull(selector);

					selector.toggleSelecting();
				}
				else
				{
					player.sendLocalizedMessage("core.mines.require_admin");
				}
			}
		}
	}

	private class CommandCreate extends Command {

		private CommandCreate()
		{
			super("create", CommandMines.this.getOwner());
			// Usage: /mine create contents gold:5;redstone:3;ender:1;stone:30 name mine_a_1
			addArgument(Mines.MINE_CONTENTS);
			addArgument(Mines.MINE_NAME);
			// /mine create permissions perm1;perm2;perm3
			addArgument(Mines.MINE_PERMISSONS);
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.select_region")) return;
			if (getSender() instanceof Player)
			{
				Player player = (Player) getSender();
				if (player.getMixin(IMineSelector.class) != null)
				{
					IMineSelector selector = player.as(IMineSelector.class);

					Validate.notNull(selector);

					if (selector.cornerA() != null && selector.cornerB() != null)
					{
						CommandMines.this.getMines().addMine(new Mine(
								(String) this.getObject("name")
								, (Set<Weighted<Material>>) this.getObject("contents")
								, new Region((String) this.getObject("name"), new Cuboid(selector.cornerA(), selector.cornerB()), 0)
								, (Set<String>) this.getObject("permissions")));
					}
					else
					{
						player.sendLocalizedMessage("core.mines.select_region.not_selected");
					}
				}
				else
				{
					player.sendLocalizedMessage("core.mines.require_admin");
				}
			}
		}
	}

	private class CommandModify extends Command {

		private CommandModify()
		{
			super("modify", CommandMines.this.getOwner());
			addArgument(Mines.MINE_NAME);
			addArgument(Mines.MINE_CONTENTS);
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.modify")) return;
			if (getSender() instanceof Player)
			{
				Player player = (Player) getSender();
				if (player.getMixin(IMineSelector.class) != null)
				{
					String name = (String) this.getObject("name");
					if (CommandMines.this.getMines().mineExists(name))
					{
						CommandMines.this.getMines().getMine(name).setContents((Set<Weighted<Material>>) this.getObject("contents"));
					}
					else
					{
						player.sendLocalizedMessage("core.mines.modify.does_not_exist");
					}
				}
				else
				{
					player.sendLocalizedMessage("core.mines.require_admin");
				}
			}
		}
	}

	private class CommandRemove extends Command {

		private CommandRemove()
		{
			super("remove", CommandMines.this.getOwner());
			addArgument(Mines.MINE_NAME);
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.remove")) return;
			if (getSender() instanceof Player)
			{
				Player player = (Player) getSender();
				if (player.getMixin(IMineSelector.class) != null)
				{
					CommandMines.this.getMines().removeMine((String) this.getObject("name"));
				}
				else
				{
					player.sendLocalizedMessage("core.mines.require_admin");
				}
			}
		}
	}

	private class CommandMoveMine extends Command {

		private CommandMoveMine()
		{
			super("move", CommandMines.this.getOwner());
			addArgument(Mines.MINE_NAME);
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.modify")) return;
			if (getSender() instanceof Player)
			{
				Player player = (Player) getSender();
				if (player.getMixin(IMineSelector.class) != null)
				{
					IMineSelector selector = player.as(IMineSelector.class);

					Validate.notNull(selector);

					String name = (String) this.getObject("name");
					if (CommandMines.this.getMines().mineExists(name))
					{
						if (selector.cornerA() != null && selector.cornerB() != null)
						{
							CommandMines.this.getMines().getMine(name).setRegion(new Region(name, new Cuboid(selector.cornerA(), selector.cornerB()), 0));
						}
						else
						{
							player.sendLocalizedMessage("core.mines.select_region.not_selected");
						}
					}
					else
					{
						player.sendLocalizedMessage("core.mines.modify.does_not_exist");
					}
				}
				else
				{
					player.sendLocalizedMessage("core.mines.require_admin");
				}
			}
		}
	}

	private class CommandSetPermissions extends Command {

		private CommandSetPermissions()
		{
			super("setpermissions", CommandMines.this.getOwner());
			addArgument(Mines.MINE_PERMISSONS);
			addArgument(Mines.MINE_NAME);
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.modify")) return;
			if (getSender() instanceof Player)
			{
				Player player = (Player) getSender();
				if (player.getMixin(IMineSelector.class) != null)
				{
					String name = (String) this.getObject("name");
					if (CommandMines.this.getMines().mineExists(name))
					{
						CommandMines.this.getMines().getMine(name).setPermissions((Set<String>) this.getObject("permissions"));
					}
					else
					{
						player.sendLocalizedMessage("core.mines.modify.does_not_exist");
					}
				}
				else
				{
					player.sendLocalizedMessage("core.mines.require_admin");
				}
			}
		}
	}

	private class CommandInfo extends Command {

		private CommandInfo()
		{
			super("info", CommandMines.this.getOwner());
			addArgument(Mines.MINE_NAME);
		}

		@Override
		public void run()
		{
			if (getSender().notHasPermission("core.mines.info")) return;
			if (getSender() instanceof Player) {
				Player player = (Player) getSender();
				String name = (String) this.getObject("name");
				Mines mines = CommandMines.this.getMines();
				if (mines.mineExists(name))
				{
					Mine mine = mines.getMine(name);
					player.sendLocalizedMessage("core.mines.info.name", name);
					player.sendLocalizedMessage("core.mines.info.permissions", StringUtils.join(mine.getPermissions(), ", "));
					player.sendLocalizedMessage("core.mines.info.contents", StringUtils.join(mine.getContents().stream().map(p -> "Block type: " + p.getValue().toString() + ", Weight: " + p.getWeight()).collect(Collectors.toSet()), ", "));
				}
			}
		}
	}
}
