package com.github.zwarunek.timemachine.util;

import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.items.ChunkWand;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class ItemListener implements Listener {

    final private TimeMachine plugin;

    public ItemListener(TimeMachine plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        try{
            if(event.getItem() != null && event.getItem().getItemMeta().getLore().equals(ChunkWand.getLore())){

                if(player.rayTraceBlocks(200) != null ) {
                    Block block = player.rayTraceBlocks(200).getHitBlock();
                    Chunk chunk = block.getChunk();
                    if(event.getAction().name().startsWith("LEFT_CLICK")){
                        if(plugin.chunkWand.addChunk(block))
                            player.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Selected Chunk x: " + chunk.getX() + " z: " + chunk.getZ());
                    }
                    else if(event.getAction().name().startsWith("RIGHT_CLICK")){
                        if(plugin.chunkWand.removeChunk(block))
                            player.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Removed Chunk x: " + chunk.getX() + " z: " + chunk.getZ());
                    }
                }
            }
        }catch(NullPointerException ignored){}
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Item drop = event.getItemDrop();
        event.getPlayer().sendMessage("Dropped " + drop.getItemStack().getType().name());
        if(drop.getItemStack().isSimilar(plugin.chunkWand.chunkWand)) {
            drop.remove();
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Chunk wand has been destroyed");
            plugin.chunkWand.isInUse = false;
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (event.getClick().isShiftClick()) {
            if (clicked == event.getWhoClicked().getInventory()) {
                ItemStack clickedOn = event.getCurrentItem();

                if (clickedOn != null && event.getCurrentItem().getItemMeta().getDisplayName() != null && event.getCurrentItem().isSimilar(plugin.chunkWand.chunkWand)) {
                    event.setCancelled( true );
                }
            }
        }

        if (clicked != event.getWhoClicked().getInventory()) { // Note: !=
            // The cursor item is going into the top inventory
            ItemStack onCursor = event.getCursor();

            if (onCursor != null && event.getCurrentItem().getItemMeta().getDisplayName() != null && event.getCurrentItem().isSimilar(plugin.chunkWand.chunkWand)) {
                event.setCancelled( true );
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack dragged = event.getOldCursor(); // This is the item that is being dragged

        if (dragged.getItemMeta().getDisplayName() != null && dragged.isSimilar(plugin.chunkWand.chunkWand)) {
            int inventorySize = event.getInventory().getSize(); // The size of the inventory, for reference

            // Now we go through all of the slots and check if the slot is inside our inventory (using the inventory size as reference)
            for (int i : event.getRawSlots()) {
                if (i < inventorySize) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

}
