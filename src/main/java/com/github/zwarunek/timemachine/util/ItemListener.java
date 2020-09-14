package com.github.zwarunek.timemachine.util;

import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.commands.GUI;
import com.github.zwarunek.timemachine.items.ChunkWand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.Collections;


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
                RayTraceResult rayTrace = player.rayTraceBlocks(200);
                if(rayTrace != null ) {
                    Block block = rayTrace.getHitBlock();

                    if(event.getAction().name().startsWith("LEFT_CLICK")){
                        plugin.chunkWand.player = player;
                        plugin.chunkWand.addChunk(block);
                    }
                    else if(event.getAction().name().startsWith("RIGHT_CLICK")){
                        plugin.chunkWand.player = player;
                        plugin.chunkWand.removeChunk(block);
                    }
                }
            }
        }catch(NullPointerException ignored){}
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Item drop = event.getItemDrop();
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

            if (onCursor != null && event.getCurrentItem()!=null && event.getCurrentItem().getItemMeta().getDisplayName() != null && event.getCurrentItem().isSimilar(plugin.chunkWand.chunkWand)) {
                event.setCancelled( true );
            }
        }

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA + "Time Machine")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Backup")){
                GUI.args = Arrays.asList("backup");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), GUI.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Restore")){
                GUI.args = Arrays.asList("restore");
                GUI.createRestore(plugin, (Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Chunk Wand")){
                GUI.args = Arrays.asList("wand");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), GUI.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Save Selected Chunks")){
                GUI.args = Arrays.asList("saveselectedchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), GUI.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Discard Saved Chunks")){
                GUI.args = Arrays.asList("discardsavedchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), GUI.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Delete Backups")){
                GUI.args = Arrays.asList("deletebackup");
                GUI.createSelectBackup(plugin, (Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Delete Backups")){
                GUI.args = Arrays.asList("deletebackup");
                GUI.createSelectBackup(plugin, (Player) event.getWhoClicked(), 1);
            }

            event.setCancelled(true);
        }
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA + "TM Restore")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Server")){
                GUI.argsAdd("server");
                event.setCancelled(true);
                GUI.createSelectBackup(plugin, (Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "World")){
                GUI.argsAdd("world");
                event.setCancelled(true);
                GUI.createSelectWorld(plugin, (Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Player")){
                GUI.argsAdd("player");
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1);
                event.setCancelled(true);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Chunk")){
                GUI.args.add("chunk");
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }

            event.setCancelled(true);
        }
        if(event.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Restore Player")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Left")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1 - Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Right")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1 + Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                GUI.createRestore(plugin, (Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Refresh")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1);
                event.setCancelled(true);
            }
            else if(clickedItem.getType().equals(Material.PLAYER_HEAD)){
                GUI.args.add(((SkullMeta)clickedItem.getItemMeta()).getOwningPlayer().getName());
                GUI.createRestorePlayer(plugin, (Player) event.getWhoClicked());
                event.setCancelled(true);
            }

            event.setCancelled(true);
        }
        if(event.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Select Backup")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Left")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1 - Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Right")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1 + Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                GUI.createRestore(plugin, (Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Refresh")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1);
                event.setCancelled(true);
            }
            else if(clickedItem.getType().equals(Material.MUSIC_DISC_13)){
                GUI.args.add((clickedItem.getItemMeta()).getDisplayName());
                plugin.command.handleCommand(event.getWhoClicked(), GUI.args.toArray(new String[0]));
            }

            event.setCancelled(true);
        }
        if(event.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Select World")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Left")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1 - Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Right")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1 + Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                GUI.createRestore(plugin, (Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Refresh")){
                GUI.createSelectPlayer(plugin, (Player) event.getWhoClicked(), 1);
                event.setCancelled(true);
            }
            else if(clickedItem.getType().equals(Material.FIREWORK_STAR)){
                GUI.args.add((clickedItem.getItemMeta()).getDisplayName());
                plugin.command.handleCommand(event.getWhoClicked(), GUI.args.toArray(new String[0]));
            }

            event.setCancelled(true);
        }
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA + "Restore Player")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "All")){
                GUI.args.add("all");
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Inventory")){
                GUI.args.add("inventory");
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Ender Chest")){
                GUI.args.add("enderchest");
            }
            event.setCancelled(true);
            GUI.createSelectBackup(plugin, (Player) event.getWhoClicked(), 1);
            event.setCancelled(true);
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
