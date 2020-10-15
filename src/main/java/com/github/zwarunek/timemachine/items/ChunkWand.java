package com.github.zwarunek.timemachine.items;

import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.util.TMChunk;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class ChunkWand{
    public TimeMachine plugin;
    public boolean isInUse = false;
    public Player player;
    public ItemStack chunkWand;
    private List<TMChunk> selectedChunks = new ArrayList<>();
    public ChunkWand(TimeMachine plugin){
        this.plugin = plugin;
        chunkWand = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = chunkWand.getItemMeta();
        meta.setDisplayName(plugin.messages.getProperty("wandName"));
        meta.setLore(getLore());
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        chunkWand.setItemMeta(meta);
    }
    public List<String> getLore(){
        List<String> lore = new ArrayList<>();
        lore.add(plugin.messages.getProperty("wandLore1"));
        lore.add(plugin.messages.getProperty("wandLore2"));
        return lore;
    }
    public ItemStack getChunkWand(Player player) {
        isInUse = true;
        this.player = player;
        return chunkWand;
    }
    public void addChunk(Block block){
        Chunk chunk = block.getChunk();
        for(TMChunk tmChunk : selectedChunks)
            if(tmChunk.getChunk().equals(chunk))
                return;
        TMChunk tmChunk = new TMChunk(chunk);

        for(TMChunk selectedtmChunk : selectedChunks) {
            Chunk selectedChunk = selectedtmChunk.getChunk();
            if (selectedChunk.getZ() - chunk.getZ() == 1 && chunk.getX() == selectedChunk.getX()) {
                tmChunk.setSouth(selectedtmChunk);
            }
            if (selectedChunk.getX() - chunk.getX() == 1 && chunk.getZ() == selectedChunk.getZ()) {
                tmChunk.setEast(selectedtmChunk);
            }
            if (chunk.getZ() - selectedChunk.getZ() == 1 && chunk.getX() == selectedChunk.getX()) {
                tmChunk.setNorth(selectedtmChunk);
            }
            if (chunk.getX() - selectedChunk.getX() == 1 && chunk.getZ() == selectedChunk.getZ()) {
                tmChunk.setWest(selectedtmChunk);
            }
        }
        selectedChunks.add(tmChunk);
        tmChunk.showBorder(player);
    }
    public void removeChunk(Block block){
        Chunk chunk = block.getChunk();
        for(TMChunk tmChunk : selectedChunks)
            if(tmChunk.getChunk().equals(chunk)){
                selectedChunks.remove(tmChunk);
                tmChunk.hideBorder(player);
                return;
            }
    }
    public List<TMChunk> getSelectedChunks(){
        return selectedChunks;
    }
    public void deselectChunks(){
//        new BukkitRunnable(){
//            @Override
//            public void run() {
        for(TMChunk chunk : selectedChunks)
            chunk.hideBorder(player);
//            }
//        }.runTask(plugin);
        selectedChunks = new ArrayList<>();
    }
}
