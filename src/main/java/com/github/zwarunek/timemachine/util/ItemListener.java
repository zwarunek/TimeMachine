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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;


public class ItemListener implements Listener {

    final private TimeMachine plugin;
    private GUI gui;

    public ItemListener(TimeMachine plugin, GUI gui){
        this.plugin = plugin;
        this.gui = gui;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        try{
            if(event.getItem() != null && event.getItem().isSimilar(plugin.chunkWand.chunkWand)){
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
            plugin.getServer().getConsoleSender().sendMessage(TimeMachine.NAME + "Chunk wand has been destroyed");
            plugin.chunkWand.isInUse = false;
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (event.getClick().isShiftClick()) {
            if (clicked == event.getWhoClicked().getInventory()) {
                ItemStack clickedOn = event.getCurrentItem();

                if (clickedOn != null && event.getCurrentItem().isSimilar(plugin.chunkWand.chunkWand)) {
                    event.setCancelled( true );
                }
            }
        }
        else if (clicked != event.getWhoClicked().getInventory()) {

            ItemStack onCursor = event.getCursor();

            if (onCursor != null && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(plugin.chunkWand.chunkWand)) {
                event.setCancelled( true );
            }
        }
        else if(event.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA + "Time Machine")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Backup")){
                gui.args = Collections.singletonList("backup");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Restore")){
                gui.args = new ArrayList<>();
                gui.args.add("restore");
                gui.createRestore((Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Chunk Wand")){
                gui.args = new ArrayList<>();
                gui.args.add("wand");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Save Selected Chunks")){
                gui.args = new ArrayList<>();
                gui.args.add("saveselectedchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Discard Saved Chunks")){
                gui.args = new ArrayList<>();
                gui.args.add("discardsavedchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Delete Backups")){
                gui.args = new ArrayList<>();
                gui.args.add("deletebackup");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
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
                gui.args.add("server");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "World")){
                gui.args.add("world");
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Player")){
                gui.args.add("player");
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Selected Chunks")){
                gui.args.add("chunk");
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
            }
            else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Back")){
                gui.args.remove(gui.args.size() - 1);
                gui.createMain((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
        if(event.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Restore Player - page")){
            ItemStack clickedItem = event.getCurrentItem();
            if(clickedItem == null){
                event.setCancelled(true);
                return;
            }
            int page = Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1));

            if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Left")){
                gui.createSelectPlayer((Player) event.getWhoClicked(), page - 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Right")){
                gui.createSelectPlayer((Player) event.getWhoClicked(), page + 1);
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Refresh")){
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getType().equals(Material.PLAYER_HEAD)){
                gui.args.add(((SkullMeta)clickedItem.getItemMeta()).getOwningPlayer().getName());
                gui.createRestorePlayer((Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "All")){
                gui.args.add((clickedItem.getItemMeta()).getDisplayName());
                gui.createRestorePlayer((Player) event.getWhoClicked());
            }
            else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Back")){
                gui.args.remove(gui.args.size() - 1);
                gui.createRestore((Player) event.getWhoClicked());
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
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 - Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.getWhoClicked().closeInventory();
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Right")){
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 + Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                gui.createRestore((Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Refresh")){
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getType().equals(Material.MUSIC_DISC_13)){
                gui.args.add((clickedItem.getItemMeta()).getDisplayName());
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Back")){
                if(gui.args.get(gui.args.size() - 1).equalsIgnoreCase("deletebackup")){
                    gui.createMain((Player) event.getWhoClicked());
                }
                else if(gui.args.get(gui.args.size() - 1).equalsIgnoreCase("server")){
                    gui.createRestore((Player) event.getWhoClicked());
                }
                else if(gui.args.contains("chunk") || gui.args.contains("world")){
                    gui.createSelectWorld((Player) event.getWhoClicked(), 1);
                }
                else if(gui.args.contains("player")){
                    gui.createRestorePlayer((Player) event.getWhoClicked());
                }
                gui.args.remove(gui.args.size() - 1);

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
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 - Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Page Right")){
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 + Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                gui.createRestore((Player) event.getWhoClicked());
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Refresh")){
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
                event.setCancelled(true);
            }
            else if(clickedItem.getType().equals(Material.FIREWORK_STAR) || clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "All")){
                gui.args.add((clickedItem.getItemMeta()).getDisplayName());
                if(gui.args.contains("chunk")) {
                    gui.args.add("selected");
                }
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Back")){
                gui.args.remove(gui.args.size() - 1);
                gui.createRestore((Player) event.getWhoClicked());

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
                gui.args.add("all");
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Inventory")){
                gui.args.add("inventory");
            }
            else if(clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Ender Chest")){
                gui.args.add("enderchest");
            }
            else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Back")){
                gui.args.remove(gui.args.size() - 1);
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1);
                return;
            }
            gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack dragged = event.getOldCursor(); // This is the item that is being dragged

        if (dragged.isSimilar(plugin.chunkWand.chunkWand)) {
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
