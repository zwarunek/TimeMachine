package com.github.zwarunek.timemachine.commands;

import com.github.zwarunek.timemachine.TimeMachine;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

public class GUI {

    public List<String> args;
    public TimeMachine plugin;

    public GUI(TimeMachine plugin){
        this.plugin = plugin;
        this.args = new ArrayList<>();
    }

    public void createMain(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "Time Machine");

        ItemStack backup = new ItemStack(Material.MUSIC_DISC_13);
        ItemStack restore = new ItemStack(Material.CLOCK);
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemStack saveSelectedChunks = new ItemStack(Material.LIME_DYE);
        ItemStack discardSelectedChunks = new ItemStack(Material.MAGENTA_DYE);
        ItemStack deleteBackups = new ItemStack(Material.MUSIC_DISC_11);

        ItemMeta backupMeta = backup.getItemMeta();
        backupMeta.setDisplayName(ChatColor.WHITE + "Backup");
        backup.setItemMeta(backupMeta);

        ItemMeta restoreMeta = restore.getItemMeta();
        restoreMeta.setDisplayName(ChatColor.WHITE + "Restore");
        restore.setItemMeta(restoreMeta);

        ItemMeta wandMeta = wand.getItemMeta();
        wandMeta.setDisplayName(ChatColor.AQUA + "Chunk Wand");
        wandMeta.addEnchant(Enchantment.LUCK, 1, false);
        wandMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wand.setItemMeta(wandMeta);

        ItemMeta saveSelectedChunksMeta = saveSelectedChunks.getItemMeta();
        saveSelectedChunksMeta.setDisplayName(ChatColor.WHITE + "Save Selected Chunks");
        saveSelectedChunks.setItemMeta(saveSelectedChunksMeta);

        ItemMeta discardSelectedChunksMeta = discardSelectedChunks.getItemMeta();
        discardSelectedChunksMeta.setDisplayName(ChatColor.WHITE + "Discard Saved Chunks");
        discardSelectedChunks.setItemMeta(discardSelectedChunksMeta);

        ItemMeta deleteBackupsMeta = deleteBackups.getItemMeta();
        deleteBackupsMeta.setDisplayName(ChatColor.WHITE + "Delete Backups");
        deleteBackups.setItemMeta(deleteBackupsMeta);

        ItemStack[] items = {backup, restore, wand, saveSelectedChunks, discardSelectedChunks, deleteBackups};
        gui.setContents(items);

        player.openInventory(gui);
    }
    public void createRestore(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "TM Restore");

        ItemStack server = new ItemStack(Material.LAVA_BUCKET);
        ItemStack world = new ItemStack(Material.FIREWORK_STAR);
        ItemStack playerFile = new ItemStack(Material.PLAYER_HEAD);
        ItemStack chunk = new ItemStack(Material.GRASS_BLOCK);

        ItemMeta serverMeta = server.getItemMeta();
        serverMeta.setDisplayName(ChatColor.WHITE + "Server");
        server.setItemMeta(serverMeta);

        ItemMeta worldMeta = world.getItemMeta();
        worldMeta.setDisplayName(ChatColor.WHITE + "World");
        world.setItemMeta(worldMeta);

        SkullMeta playerFileMeta = (SkullMeta)playerFile.getItemMeta();
        playerFileMeta.setDisplayName(ChatColor.WHITE + "Player");
        playerFileMeta.setOwningPlayer(player);
        playerFile.setItemMeta(playerFileMeta);

        ItemMeta chunkMeta = chunk.getItemMeta();
        chunkMeta.setDisplayName(ChatColor.WHITE + "Selected Chunks");
        chunk.setItemMeta(chunkMeta);

        ItemStack[] items = {server, world, playerFile, chunk};
        gui.setContents(items);

        player.openInventory(gui);
    }
    public void createSelectPlayer(Player player, int page){
        int size = 54;
        Inventory gui = Bukkit.createInventory(player, size, ChatColor.DARK_AQUA + "Restore Player - page " + page);
        int playersPerPage = size - 9;
        ItemStack blank = new ItemStack(Material.AIR);
        ItemStack refresh = new ItemStack(Material.LIME_DYE);
        ItemStack pageRight = new ItemStack(Material.PAPER);
        ItemStack pageLeft = new ItemStack(Material.PAPER);

        ItemMeta refreshMeta = refresh.getItemMeta();
        refreshMeta.setDisplayName(ChatColor.WHITE + "Refresh");
        refresh.setItemMeta(refreshMeta);

        ItemMeta pageRightMeta = pageRight.getItemMeta();
        pageRightMeta.setDisplayName(ChatColor.WHITE + "Page Right");
        pageRight.setItemMeta(pageRightMeta);

        ItemMeta pageLeftMeta = pageLeft.getItemMeta();
        pageLeftMeta.setDisplayName(ChatColor.WHITE + "Page Left");
        pageLeft.setItemMeta(pageLeftMeta);

        ArrayList<ItemStack> items = new ArrayList<>();
//        List<OfflinePlayer> players = Arrays.asList(plugin.offlinePlayers);
        List<OfflinePlayer> players = new ArrayList<>();
        for(int i = 0; i < plugin.offlinePlayers.length; i++){
            for(int j = 0; j < 40; j++)
                players.add(plugin.offlinePlayers[i]);
        }
        if(players.subList(playersPerPage * (page - 1), players.size()).size() >= playersPerPage)
            players = players.subList(playersPerPage * (page - 1), playersPerPage * page);
        else if(players.isEmpty())
            players = new ArrayList<>();
        else
            players = players.subList(playersPerPage * (page - 1), players.size());
        OfflinePlayer player1;
        boolean addRightButton = true;
        for(int i = 0; i<playersPerPage; i++){
            if(i<players.size()) {
                player1 = players.get(i);
                ItemStack playerStack = new ItemStack(Material.PLAYER_HEAD);

                SkullMeta playerFileMeta = (SkullMeta) playerStack.getItemMeta();
                playerFileMeta.setOwningPlayer(player1);
                playerStack.setItemMeta(playerFileMeta);

                items.add(playerStack);
            }
            else{
                items.add(new ItemStack(Material.AIR));
                addRightButton = false;
            }
        }

        items.addAll(Arrays.asList(blank, blank, blank, page != 1 ? pageLeft : blank, refresh, addRightButton ? pageRight : blank, blank, blank, blank));
        gui.setContents(items.toArray(new ItemStack[size]));

        player.openInventory(gui);
    }
    public void createRestorePlayer(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.DARK_AQUA + "Restore Player");

        ItemStack all = new ItemStack(Material.EMERALD);
        ItemStack inventory = new ItemStack(Material.CHEST);
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST);

        ItemMeta allMeta = all.getItemMeta();
        allMeta.setDisplayName(ChatColor.WHITE + "All");
        all.setItemMeta(allMeta);

        ItemMeta inventoryMeta = inventory.getItemMeta();
        inventoryMeta.setDisplayName(ChatColor.WHITE + "Inventory");
        inventory.setItemMeta(inventoryMeta);

        ItemMeta enderChestMeta = enderChest.getItemMeta();
        enderChestMeta.setDisplayName(ChatColor.WHITE + "Ender Chest");
        enderChest.setItemMeta(enderChestMeta);


        ItemStack[] items = {all, inventory, enderChest};
        gui.setContents(items);

        player.openInventory(gui);
    }
    public void createSelectBackup(Player player, int page){
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA + "Select Backup - page " + page);
        int filesPerPage = 54 - 9;
        ItemStack blank = new ItemStack(Material.AIR);
        ItemStack refresh = new ItemStack(Material.LIME_DYE);
        ItemStack pageRight = new ItemStack(Material.PAPER);
        ItemStack pageLeft = new ItemStack(Material.PAPER);

        ItemMeta refreshMeta = refresh.getItemMeta();
        refreshMeta.setDisplayName(ChatColor.WHITE + "Refresh");
        refresh.setItemMeta(refreshMeta);

        ItemMeta pageRightMeta = pageRight.getItemMeta();
        pageRightMeta.setDisplayName(ChatColor.WHITE + "Page Right");
        pageRight.setItemMeta(pageRightMeta);

        ItemMeta pageLeftMeta = pageLeft.getItemMeta();
        pageLeftMeta.setDisplayName(ChatColor.WHITE + "Page Left");
        pageLeft.setItemMeta(pageLeftMeta);

        ArrayList<ItemStack> items = new ArrayList<>();
        List<File> files = plugin.backupList;
        if(files.subList(filesPerPage * (page - 1), files.size()).size() >= filesPerPage)
            files = files.subList(filesPerPage * (page - 1), filesPerPage * page);
        else if(files.isEmpty())
            files = new ArrayList<>();
        else
            files = files.subList(filesPerPage * (page - 1), files.size());
        File file;
        for(int i = 0; i<filesPerPage; i++){
            if(i<files.size()) {
                file = files.get(i);
                ItemStack fileStack = new ItemStack(Material.MUSIC_DISC_13);

                ItemMeta fileMeta = fileStack.getItemMeta();
                fileMeta.setDisplayName(file.getName());
                fileStack.setItemMeta(fileMeta);
                items.add(fileStack);
            }
            else{
                items.add(new ItemStack(Material.AIR));
            }
        }

        items.addAll(Arrays.asList(blank, blank, blank, pageLeft, refresh, pageRight, blank, blank, blank));
        gui.setContents(items.toArray(new ItemStack[54]));

        player.openInventory(gui);
    }
    public void createSelectWorld(Player player, int page){
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA + "Select World - page " + page);
        int filesPerPage = 54 - 9;
        ItemStack blank = new ItemStack(Material.AIR);
        ItemStack refresh = new ItemStack(Material.LIME_DYE);
        ItemStack pageRight = new ItemStack(Material.PAPER);
        ItemStack pageLeft = new ItemStack(Material.PAPER);

        ItemMeta refreshMeta = refresh.getItemMeta();
        assert refreshMeta != null;
        refreshMeta.setDisplayName(ChatColor.WHITE + "Refresh");
        refresh.setItemMeta(refreshMeta);

        ItemMeta pageRightMeta = pageRight.getItemMeta();
        pageRightMeta.setDisplayName(ChatColor.WHITE + "Page Right");
        pageRight.setItemMeta(pageRightMeta);

        ItemMeta pageLeftMeta = pageLeft.getItemMeta();
        pageLeftMeta.setDisplayName(ChatColor.WHITE + "Page Left");
        pageLeft.setItemMeta(pageLeftMeta);

        ArrayList<ItemStack> items = new ArrayList<>();
        List<World> worlds = Bukkit.getWorlds();
        if(worlds.subList(filesPerPage * (page - 1), worlds.size()).size() >= filesPerPage)
            worlds = worlds.subList(filesPerPage * (page - 1), filesPerPage * page);
        else if(worlds.isEmpty())
            worlds = new ArrayList<>();
        else
            worlds = worlds.subList(filesPerPage * (page - 1), worlds.size());
        World world;
        for(int i = 0; i<filesPerPage; i++){
            if(i<worlds.size()) {
                world = worlds.get(i);
                ItemStack worldStack = new ItemStack(Material.FIREWORK_STAR);

                ItemMeta fileMeta = worldStack.getItemMeta();
                fileMeta.setDisplayName(world.getName());
                worldStack.setItemMeta(fileMeta);
                items.add(worldStack);
            }
            else{
                items.add(new ItemStack(Material.AIR));
            }
        }

        items.addAll(Arrays.asList(blank, blank, blank, pageLeft, refresh, pageRight, blank, blank, blank));
        gui.setContents(items.toArray(new ItemStack[54]));

        player.openInventory(gui);
    }

}
