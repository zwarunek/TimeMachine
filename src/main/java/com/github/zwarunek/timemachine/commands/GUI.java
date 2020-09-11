package com.github.zwarunek.timemachine.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI {
    Player player;
    public GUI(Player player){
        this.player = player;
        create();
    }

    private void create() {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "Time Machine GUI");

        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta wandMeta = wand.getItemMeta();
        wandMeta.setDisplayName(ChatColor.AQUA + "Chunk Wand");
        wandMeta.addEnchant(Enchantment.LUCK, 1, false);
        wandMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wand.setItemMeta(wandMeta);
        ItemStack[] items = {wand};
        gui.setContents(items);

        player.openInventory(gui);
    }

}
