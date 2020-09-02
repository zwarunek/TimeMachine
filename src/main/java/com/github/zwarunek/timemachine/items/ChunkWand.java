package com.github.zwarunek.timemachine.items;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChunkWand{
    public boolean isInUse = false;
    public Player player;
    public ItemStack chunkWand;
    private List<Chunk> selectedChunks = new ArrayList<>();
    public ChunkWand(){
        chunkWand = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = chunkWand.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "|" + ChatColor.AQUA + "Chunk Wand" + ChatColor.DARK_AQUA + "|");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Select Chunks by left click");
        lore.add(ChatColor.GRAY + "Deselect Chunks by right click");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        chunkWand.setItemMeta(meta);
    }
    public static List<String> getLore(){
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Select Chunks by left click");
        lore.add(ChatColor.GRAY + "Deselect Chunks by right click");
        return lore;
    }
    public ItemStack getChunkWand(Player player) {
        isInUse = true;
        this.player = player;
        return chunkWand;
    }
    public boolean addChunk(Chunk chunk){
        if(!selectedChunks.contains(chunk)) {
            selectedChunks.add(chunk);
            return true;
        }
        return false;
    }
    public boolean removeChunk(Chunk chunk){
        if(selectedChunks.contains(chunk)) {
            selectedChunks.remove(chunk);
            return true;
        }
        return false;
    }
    public List<Chunk> getSelectedChunks(){
        return selectedChunks;
    }
    public void deselectChunks(){
        selectedChunks = new ArrayList<>();
    }
}
