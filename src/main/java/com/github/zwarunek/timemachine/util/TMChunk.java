package com.github.zwarunek.timemachine.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TMChunk {
    Chunk chunk;
    TMChunk north;
    TMChunk south;
    TMChunk east;
    TMChunk west;
    Hashtable<String, List<Block>> changedBlocks;
    public TMChunk(Chunk chunk){
        this.chunk = chunk;
        changedBlocks = new Hashtable<>();
        this.north = null;
        this.south = null;
        this.east = null;
        this.west = null;
        List<Block> corners = new ArrayList<>();
        List<Block> northEdge = new ArrayList<>();
        List<Block> southEdge = new ArrayList<>();
        List<Block> eastEdge = new ArrayList<>();
        List<Block> westEdge = new ArrayList<>();
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                if((z == 0 || z == 15) && (x == 0 || x == 15)){
                    corners.addAll(findBlocks(x, z));
                }
                if(z == 0 && x != 0 && x != 15){
                    northEdge.addAll(findBlocks(x, z));
                }
                if(z == 15 && x != 0 && x != 15){
                    southEdge.addAll(findBlocks(x, z));
                }
                if(x == 0 && z != 0 && z != 15){
                    westEdge.addAll(findBlocks(x, z));
                }
                if(x == 15 && z != 0 && z != 15){
                    eastEdge.addAll(findBlocks(x, z));
                }
            }
        }
        changedBlocks.put("corners", corners);
        changedBlocks.put("northEdge", northEdge);
        changedBlocks.put("southEdge", southEdge);
        changedBlocks.put("eastEdge", eastEdge);
        changedBlocks.put("westEdge", westEdge);
    }
    private List<Block> findBlocks(int x, int z){
        List<Block> blocks = new ArrayList<>();
        for(int y = 0; y<256; y++){
            Block changingBlock = chunk.getBlock(x, y, z);
            if(!changingBlock.getBlockData().getMaterial().equals(Material.AIR)){
                blocks.add(changingBlock);
            }
        }
        return blocks;
    }
    public Chunk getChunk() {
        return chunk;
    }

    public TMChunk getEast() {
        return east;
    }

    public TMChunk getNorth() {
        return north;
    }

    public TMChunk getSouth() {
        return south;
    }

    public TMChunk getWest() {
        return west;
    }
    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }
    public Hashtable<String, List<Block>> getChangedBlocks() {
        return changedBlocks;
    }

    public void setEast(TMChunk east) {
        this.east = east;
        if(east != null)
            this.east.west = this;
    }

    public void setNorth(TMChunk north) {
        this.north = north;
        if(north != null)
            this.north.south = this;
    }

    public void setSouth(TMChunk south) {
        this.south = south;
        if(south != null)
            this.south.north = this;
    }

    public void setWest(TMChunk west) {
        this.west = west;
        if(west != null)
            this.west.east = this;
    }
    public void setChangedBlocks(Hashtable<String, List<Block>> changedBlocks) {
        this.changedBlocks = changedBlocks;
    }

    public void showBorder(Player player) {
        showSide(player, "corners");
        if(north == null){
            showSide(player, "northEdge");
        }
        else{
            north.hideSide(player, "southEdge");
        }
        if(south == null){
            showSide(player, "southEdge");
        }
        else{
            south.hideSide(player, "northEdge");
        }
        if(east == null){
            showSide(player, "eastEdge");
        }
        else{
            east.hideSide(player, "westEdge");
        }
        if(west == null){
            showSide(player, "westEdge");
        }
        else{
            west.hideSide(player, "eastEdge");
        }
    }
    public void hideBorder(Player player) {
        hideSide(player, "corners");
        hideSide(player, "northEdge");
        hideSide(player, "southEdge");
        hideSide(player, "eastEdge");
        hideSide(player, "westEdge");
        if(north != null){
            north.showSide(player, "southEdge");
            north.setSouth(null);
        }
        if(south != null){
            south.showSide(player, "northEdge");
            south.setNorth(null);
        }
        if(east != null){
            east.showSide(player, "westEdge");
            east.setWest(null);
        }
        if(west != null){
            west.showSide(player, "eastEdge");
            west.setEast(null);
        }

    }
    public void hideSide(Player player, String side){
        for(Block block : changedBlocks.get(side))
            player.sendBlockChange(block.getLocation(), block.getBlockData());
    }
    public void showSide(Player player, String side){
        for(Block block : changedBlocks.get(side))
            player.sendBlockChange(block.getLocation(), Bukkit.createBlockData(Material.GOLD_BLOCK));
    }
}

