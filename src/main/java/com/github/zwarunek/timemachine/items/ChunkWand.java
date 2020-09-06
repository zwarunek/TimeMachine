package com.github.zwarunek.timemachine.items;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ChunkWand{
    public boolean isInUse = false;
    public Player player;
    public ItemStack chunkWand;
    private List<Chunk> selectedChunks = new ArrayList<>();
    private Hashtable<Chunk, List<Block>> changedBlocks = new Hashtable<>();
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
    public boolean addChunk(Block block){
        Chunk chunk = block.getChunk();
        if(!selectedChunks.contains(chunk)) {
            selectedChunks.add(chunk);
            displayBorder(chunk, block);
            return true;
        }
        return false;
    }
    public boolean removeChunk(Block block){
        Chunk chunk = block.getChunk();
        if(selectedChunks.contains(chunk)) {
            selectedChunks.remove(chunk);
            undisplayBorder(chunk);
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
    private void displayBorder(Chunk chunk, Block block){
        List<Block> blocks = new ArrayList<>();
        boolean north, south, east, west;
        north = south = east = west = false;
        for(Chunk selectedChunk : selectedChunks){
            if(chunk.getZ() != selectedChunk.getZ() || chunk.getX() != selectedChunk.getX()) {
                if (selectedChunk.getZ() - chunk.getZ() == 1 && chunk.getX() == selectedChunk.getX()) {
                    south = true;
                    for(Block oldBlock : changedBlocks.get(selectedChunk)){
                        int xChunk = oldBlock.getX()%16<0?oldBlock.getX()%16+16:oldBlock.getX()%16;
                        int zChunk = oldBlock.getZ()%16<0?oldBlock.getZ()%16+16:oldBlock.getZ()%16;
                        if(xChunk >0 && xChunk <15 && zChunk == 0)
                            player.sendBlockChange(oldBlock.getLocation(), oldBlock.getBlockData());
                    }
                }
                if (selectedChunk.getX() - chunk.getX() == 1 && chunk.getZ() == selectedChunk.getZ()) {
                    east = true;
                    for(Block oldBlock : changedBlocks.get(selectedChunk)){
                        int xChunk = oldBlock.getX()%16<0?oldBlock.getX()%16+16:oldBlock.getX()%16;
                        int zChunk = oldBlock.getZ()%16<0?oldBlock.getZ()%16+16:oldBlock.getZ()%16;
                        if(zChunk >0 && zChunk <15 && xChunk == 0)
                            player.sendBlockChange(oldBlock.getLocation(), oldBlock.getBlockData());
                    }
                }
                if (chunk.getZ() - selectedChunk.getZ() == 1 && chunk.getX() == selectedChunk.getX()) {
                    north = true;
                    for(Block oldBlock : changedBlocks.get(selectedChunk)){
                        int xChunk = oldBlock.getX()%16<0?oldBlock.getX()%16+16:oldBlock.getX()%16;
                        int zChunk = oldBlock.getZ()%16<0?oldBlock.getZ()%16+16:oldBlock.getZ()%16;
                        if(xChunk >0 && xChunk <15 && zChunk == 15)
                            player.sendBlockChange(oldBlock.getLocation(), oldBlock.getBlockData());
                    }
                }
                if (chunk.getX() - selectedChunk.getX() == 1 && chunk.getZ() == selectedChunk.getZ()) {
                    west = true;
                    for(Block oldBlock : changedBlocks.get(selectedChunk)){
                        int xChunk = oldBlock.getX()%16<0?oldBlock.getX()%16+16:oldBlock.getX()%16;
                        int zChunk = oldBlock.getZ()%16<0?oldBlock.getZ()%16+16:oldBlock.getZ()%16;
                        if(zChunk >0 && zChunk <15 && xChunk == 15)
                            player.sendBlockChange(oldBlock.getLocation(), oldBlock.getBlockData());
                    }
                }

            }

        }
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                if((z == 0 && !north) || (z == 15 && !south) || (x == 0 && !west) || (x == 15 && !east)){
//                if((z == 0) || (z == 15) || (x == 0) || (x == 15)){
                    int y = block.getY();
                    Block changingBlock = chunk.getBlock(x, y, z);
                    while(y <=256 && y>0){
                        Material changeMat = changingBlock.getBlockData().getMaterial();
                        Material aboveChange = chunk.getBlock(x, y+1, z).getBlockData().getMaterial();
                        if(changeMat != Material.AIR &&
                                aboveChange == Material.AIR){
                            break;
                        }
                        else if(changeMat == Material.AIR){
                            y--;
                        }
                        else{
                            y++;
                        }
                        changingBlock = chunk.getBlock(x, y, z);
                    }
                    player.sendBlockChange(changingBlock.getLocation(), Bukkit.createBlockData(Material.GOLD_BLOCK));
                    blocks.add(changingBlock);
                }
            }
        }
        changedBlocks.put(chunk, blocks);
    }
    private void undisplayBorder(Chunk chunk) {
        for (Block block : changedBlocks.get(chunk)) {
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        }
        changedBlocks.remove(chunk);
    }
}
