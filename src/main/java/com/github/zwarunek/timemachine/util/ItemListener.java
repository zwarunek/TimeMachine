package com.github.zwarunek.timemachine.util;

import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.commands.GUI;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
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
import java.util.Collections;


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
            plugin.getServer().getConsoleSender().sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("wandDestroyed"));
            plugin.chunkWand.isInUse = false;
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getClick().isCreativeAction())
            return;
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
        String inventory = event.getView().getTitle();
        String button = clickedItem.getItemMeta().getDisplayName();
        if(inventory.equalsIgnoreCase(plugin.messages.getProperty("timeMachineInv"))){
            
            if(button.equals(plugin.messages.getProperty("backupBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = Collections.singletonList("backup");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals(plugin.messages.getProperty("restoreBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("restore");
                gui.createRestore((Player) event.getWhoClicked());
            }
            else if(button.equals(plugin.messages.getProperty("wandBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("wand");
                gui.createWand((Player) event.getWhoClicked());
            }
            else if(button.equals(plugin.messages.getProperty("deleteBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args = new ArrayList<>();
                gui.args.add("deletebackup");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            event.setCancelled(true);
        }
        if(inventory.equalsIgnoreCase(plugin.messages.getProperty("wandInv"))){
            if(button.equals(plugin.messages.getProperty("giveBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("give");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals(plugin.messages.getProperty("cancelBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("cancel");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals(plugin.messages.getProperty("selectBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("selectchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if(button.equals(plugin.messages.getProperty("deselectBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("deselectchunks");
                event.getWhoClicked().closeInventory();
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
            }
            else if (button.equals(plugin.messages.getProperty("backBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createMain((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
        if(inventory.equalsIgnoreCase(plugin.messages.getProperty("restoreInv"))){
            if(button.equals(plugin.messages.getProperty("serverBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("server");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals(plugin.messages.getProperty("worldBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("world");
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals(plugin.messages.getProperty("playerBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("player");
                gui.createSelectPlayer((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals(plugin.messages.getProperty("chunksBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("chunk");
                gui.createSelectWorld((Player) event.getWhoClicked(), 1);
            }
            else if (button.equals(plugin.messages.getProperty("backBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createMain((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
        if(inventory.startsWith(plugin.messages.getProperty("selectPlayerInv").replaceAll("%PAGE%", ""))){
            int page = Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1));

            if(button.equals(plugin.messages.getProperty("pageLeftBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), page - 1);
            }
            else if(button.equals(plugin.messages.getProperty("pageRightBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectPlayer((Player) event.getWhoClicked(), page + 1);
            }
            else if(button.equals(plugin.messages.getProperty("refreshBtnName"))){
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
            else if (button.equals(plugin.messages.getProperty("backBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createRestore((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
        if(inventory.startsWith(plugin.messages.getProperty("selectBackupInv").replaceAll("%PAGE%", ""))){
            int page = Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1));
            if(button.equals(plugin.messages.getProperty("pageLeftBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectBackup((Player) event.getWhoClicked(), page - 1);
            }
            else if(button.equals(plugin.messages.getProperty("pageRightBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectBackup((Player) event.getWhoClicked(), page + 1);
            }
            else if(button.equals(plugin.messages.getProperty("refreshBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(clickedItem.getType().equals(Material.MUSIC_DISC_13)){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add((clickedItem.getItemMeta()).getDisplayName());
                plugin.command.handleCommand(event.getWhoClicked(), gui.args.toArray(new String[0]));
                event.getWhoClicked().closeInventory();
            }
            else if (button.equals(plugin.messages.getProperty("backBtnName"))){
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
        if(inventory.startsWith(plugin.messages.getProperty("selectWorldInv").replaceAll("%PAGE%", ""))){
            int page = Integer.parseInt(event.getView().getTitle().substring(event.getView().getTitle().length() - 1));
            if(button.equals(plugin.messages.getProperty("pageLeftBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectWorld((Player) event.getWhoClicked(), page - 1);
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
            else if(button.equals(plugin.messages.getProperty("pageRightBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.createSelectWorld((Player) event.getWhoClicked(), page + 1);
            }
            else if(button.equals(plugin.messages.getProperty("refreshBtnName"))){
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
            else if (button.equals(plugin.messages.getProperty("backBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.remove(gui.args.size() - 1);
                gui.createRestore((Player) event.getWhoClicked());

            }
            event.setCancelled(true);
        }
        if(inventory.equalsIgnoreCase(plugin.messages.getProperty("restorePlayerInv"))){
            if(button.equals(plugin.messages.getProperty("allBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("all");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals(plugin.messages.getProperty("inventoryBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("inventory");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if(button.equals(plugin.messages.getProperty("enderChestBtnName"))){
                player.playEffect(player.getLocation(), Effect.CLICK2, 0);
                gui.args.add("enderchest");
                gui.createSelectBackup((Player) event.getWhoClicked(), 1);
            }
            else if (button.equals(plugin.messages.getProperty("backBtnName"))){
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
