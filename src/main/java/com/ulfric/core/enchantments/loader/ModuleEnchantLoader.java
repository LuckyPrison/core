package com.ulfric.core.enchantments.loader;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.core.Core;
import com.ulfric.core.enchantments.StateEnchantment;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;

public class ModuleEnchantLoader extends Module {

    public ModuleEnchantLoader()
    {
        super("enchantloader", "Loads dynamic enchantments from the disk", "1.0.0-REL", "Packet");

        this.addCommand(new CommandEnchants());
    }

    @Override
    public void onFirstEnable()
    {
        EnchantmentLoader.impl = new EnchantmentLoader.IEnchantmentLoader() {
            private final Map<EnchantmentType, Set<? extends Enchantment>> enchants = Maps.newEnumMap(EnchantmentType.class);

            {
                this.inst();
            }

            private void inst()
            {
                for (EnchantmentType type : EnchantmentType.values())
                {
                    this.enchants.put(type, Sets.newHashSet());
                }
                File folder = new File(Core.getPlugin(Core.class).getDataFolder(), "enchants");

                if (folder.mkdirs()) return;

                for (File file : folder.listFiles())
                {
                    String name = file.getName();

                    name = name.substring(0, name.length() - 4);

                    FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

                    EnchantmentType type = EnchantmentType.valueOf(conf.getString("type").toUpperCase());

                    // TODO Continue reimplementation
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T extends Enchantment> Set<T> getEnchants(EnchantmentType type)
            {
                return (Set<T>) this.enchants.get(type);
            }
        };

        StateEnchantment.impl = new StateEnchantment.IStateEnchantmentImpl() {
            @Override
            public boolean shouldAct(Player player)
            {
                if (player.getItemInHand() != null)
                {
                    ItemMeta meta = player.getItemInHand().getItemMeta();

                    Set<StateEnchantment> enchs = EnchantmentLoader.getEnchants(EnchantmentType.STATE);

                    for (Enchantment ench : meta.getEnchants().keySet())
                    {
                        if (!(ench instanceof StateEnchantment)) continue;

                        if (!enchs.contains(ench)) continue;

                        StateEnchantment stateEnchantment = ((StateEnchantment) ench);

                        if (meta.getEnchantLevel(stateEnchantment) == 1)
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    class CommandEnchants extends Command {

        public CommandEnchants()
        {
            super("enchants", ModuleEnchantLoader.this);
        }

        @Override
        public void run()
        {
            // TODO
        }
    }
}
