package com.github.zwarunek.timemachine.util;

import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.commands.GUI;
import com.github.zwarunek.timemachine.items.ChunkWand;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        Player player = (Player) event.getWhoClicked();
        if (event.getClick().isShiftClick()) {
            if (clicked == event.getWhoClicked().getInventory()) {
                ItemStack clickedOn = event.getCurrentItem();

                if (clickedOn != null && event.getCurrentItem().isSimilar(plugin.chunkWand.chunkWand)) {
                    event.setCancelled( true );
                }
            }
        }
        if (clicked != event.getWhoClicked().getInventory()) {

            ItemStack onCursor = event.getCursor();

            if (onCursor != null && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(plugin.chunkWand.chunkWand)) {
                event.setCancelled( true );
            }
        }
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null){
            event.setCancelled(true);
            return;
        }
        String inventory = ChatColor.stripColor(event.getView().getTitle());
        String displayName = clickedItem.getItemMeta().getDisplayName();
        String button = ChatColor.stripColor(displayName);
        if(inventory.equalsIgnoreCase("Time Machine")){
            
            if(button.equals("Backup")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = Collections.singletonList("backup");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals("Restore")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("restore");
                gui.createRestore((Player) event.getWhoClicked());
            }
            else if(button.equals("Chunk Wand")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("wand");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals("Save Selected Chunks")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("saveselectedchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals("Discard Saved Chunks")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("discardsavedchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals("Delete Backups")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("deletebackup");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            event.setCancelled(true);
        }
        if(inventory.equalsIgnoreCase("TM Restore")){
            if(button.equals("Server")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("server");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals("World")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("world");
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals("Player")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("player");
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals("Selected Chunks")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("chunk");
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
            }
            else if (button.equals("Back")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createMain((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
        if(inventory.startsWith("Restore Player - page")){
            int page = Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1));

            if(button.equals("Page Left")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), page - 1);
            }
            else if(button.equals("Page Right")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), page + 1);
            }
            else if(button.equals("Refresh")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getType().equals(Material.PLAYER_HEAD)){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add(((SkullMeta)clickedItem.getItemMeta()).getOwningPlayer().getName());
                gui.createRestorePlayer((Player) event.getWhoClicked());
            }
            else if(button.equals("All")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("all");
                gui.createRestorePlayer((Player) event.getWhoClicked());
            }
            else if (button.equals("Back")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createRestore((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
        if(inventory.startsWith("Select Backup")){
            if(button.equals("Page Left")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 - Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.getWhoClicked().closeInventory();
            }
            else if(button.equals("Page Right")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 + Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                gui.createRestore((Player) event.getWhoClicked());
            }
            else if(button.equals("Refresh")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getType().equals(Material.MUSIC_DISC_13)){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add((clickedItem.getItemMeta()).getDisplayName());
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
                event.getWhoClicked().closeInventory();
            }
            else if (button.equals("Back")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                if(gui.args.get(gui.args.size() - 1).equalsIgnoreCase("deletebackup")){
                    gui.createMain((Player) event.getWhoClicked());
                }
                else if(gui.args.get(gui.args.size() - 1).equalsIgnoreCase("server")){
                    player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                    gui.createRestore((Player) event.getWhoClicked());
                }
                else if(gui.args.contains("chunk") || gui.args.contains("world")){
                    player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                    gui.createSelectWorld((Player) event.getWhoClicked(), 1);
                }
                else if(gui.args.contains("player")){
                    player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                    gui.createRestorePlayer((Player) event.getWhoClicked());
                }
                gui.args.remove(gui.args.size() - 1);

            }
            event.setCancelled(true);
        }
        if(inventory.startsWith("Select World")){
            if(button.equals("Page Left")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 - Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
            else if(button.equals("Page Right")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1 + Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1)));
                gui.createRestore((Player) event.getWhoClicked());
            }
            else if(button.equals("Refresh")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getType().equals(Material.FIREWORK_STAR) || button.equals("All")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add((clickedItem.getItemMeta()).getDisplayName());
                if(gui.args.contains("chunk")) {
                    gui.args.add("selected");
                }
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if (button.equals("Back")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createRestore((Player) event.getWhoClicked());

            }
            event.setCancelled(true);
        }
        if(inventory.equalsIgnoreCase("Restore Player")){
            if(button.equals("All")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("all");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals("Inventory")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("inventory");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals("Ender Chest")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("enderchest");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if (button.equals("Back")){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1);
                return;
            }
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
