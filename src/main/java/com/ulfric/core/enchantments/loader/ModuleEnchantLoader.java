package com.ulfric.core.enchantments.loader;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.core.Core;
import com.ulfric.core.enchantments.*;
import com.ulfric.core.gui.PanelEnchants;
import com.ulfric.lib.coffee.command.Command;
import com.ulfric.lib.coffee.module.Module;
import com.ulfric.lib.craft.entity.player.Player;
import com.ulfric.lib.craft.inventory.item.enchant.EnchantUtils;
import com.ulfric.lib.craft.inventory.item.enchant.Enchantment;
import com.ulfric.lib.craft.inventory.item.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class ModuleEnchantLoader extends Module {

    public ModuleEnchantLoader()
    {
        super("enchantloader", "Loads dynamic enchantments from the disk", "1.0.0-REL", "Packet");

        this.addCommand(new CommandEnchants());
    }

    @Override
    public void onFirstEnable()
    {

        EnchantUtils.setAcceptingNewEnchantsMutable(true);

        /*
        EnchantUtils.registerCustomEnchantments(EnchantmentFlight.get(),
                EnchantmentBlasting.get(),
                EnchantmentNeverbreaking.get(),
                EnchantmentSpeedygonzales.get(),
                EnchantmentMagic.get(),
                EnchantmentAutoSell.get()
        );

         */
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
                if(player.getMainHand() != null){
                    ItemMeta meta = player.getMainHand().getMeta();

                    // TODO redo
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
          Player player = (Player) this.getSender();
            PanelEnchants.get().open(player);
        }
    }
}
