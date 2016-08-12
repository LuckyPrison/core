package com.ulfric.core.gui;

import com.ulfric.lib.coffee.string.Strings;
import com.ulfric.lib.craft.event.inventory.InventoryClickEvent;
import com.ulfric.lib.craft.inventory.Inventory;
import com.ulfric.lib.craft.inventory.InventoryUtils;
import com.ulfric.lib.craft.inventory.item.ItemStack;
import com.ulfric.lib.craft.panel.Panel;
import org.bukkit.ChatColor;

public class PanelEnchants extends Panel {

    private static PanelEnchants inst;

    public static PanelEnchants get() {
        return inst;
    }

    private final Inventory inventory;

    public PanelEnchants()
    {
        PanelEnchants.inst = this;
        this.inventory = InventoryUtils.newInventory(18, ChatColor.BOLD + Strings.EMPTY + ChatColor.BLUE + "Lucky Prison Enchantments");

        this.addItem(0, "Blasting", "Grants you the ability to blast extra blocks away while mining!");
        this.addItem(1, "Circles", "Mine a circle around your pickaxe with this enchant!");
        this.addItem(2, "Flight", "When you hold an item with this enchantment, you will fly!");
        this.addItem(3, "Night Vision", "Seeing in the dark won't be a problem!");
        this.addItem(4, "Speedy Gonzales", "They won't even see you coming, you'll be so... speedy.");
        this.addItem(5, "Jump", "Can Superman fly, or does he just jump REALLY high?");
        this.addItem(6, "Adam", "Draws the owners name in the floor when you mine!");
        this.addItem(7, "Creeper", "A creeper face will be broken into the blocks around where you mine.");
        this.addItem(8, "Haste", "You'll mine super fast and stuff.");
        this.addItem(9, "Pitchfork", "Carve a two-pronged spear into the ground.");
        this.addItem(10, "Resistance", "Your skin will be so thick, you won't even need a shield!");
        this.addItem(11, "Rings", "Create a mini-Saturn with every swing of your pickaxe.");
        this.addItem(12, "Saturation", "Never get the munchies!");
        this.addItem(13, "Triangles", "Just like circles, except with, you know, triangles.");
        this.addItem(14, "Trident", "It's like Pitchfork, but with an extra prong!");
        this.addItem(16, "X", "The letter X is always a cool letter. Draw it everywhere!");
        this.addItem(17, "Magic", "Gives you EXP when you mine");
        this.addItem(18, "AutoSell", "Sell all blocks in your inventory instantly!");
    }

    @Override
    protected void onInventoryClick(InventoryClickEvent event)
    {
        event.setCancelled(true);
    }

    @Override
    protected boolean render(com.ulfric.lib.craft.entity.player.Player player)
    {
        return player.inv().open(this.inventory);
    }

    private void addItem(int slot, ItemStack stack)
    {
        this.inventory.setItem(slot, stack);
    }

    private void addItem(int slot, String name, String... lore)
    {
        this.addItem(slot, this.createItem(name, lore));
    }


    private ItemStack createItem(String name, String... lore)
    {
        return null;
    }
}
