package com.github.zwarunek.timemachine.commands;

import com.github.zwarunek.timemachine.TimeMachine;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
    public ItemStack blank;
    public ItemStack refresh;
    public ItemStack pageRight;
    public ItemStack pageLeft;
    public ItemStack back;

    public GUI(TimeMachine plugin){
        this.plugin = plugin;
        this.args = new ArrayList<>();
        this.blank = new ItemStack(Material.AIR);
        this.refresh = new ItemStack(Material.LIME_DYE);
        this.pageRight = new ItemStack(Material.PAPER);
        this.pageLeft = new ItemStack(Material.PAPER);
        this.back = new ItemStack(Material.BARRIER);

        ItemMeta refreshMeta = refresh.getItemMeta();
        ItemMeta pageRightMeta = pageRight.getItemMeta();
        ItemMeta pageLeftMeta = pageLeft.getItemMeta();
        ItemMeta backMeta = back.getItemMeta();
        if(refreshMeta == null || pageRightMeta == null || pageLeftMeta == null || backMeta == null)
            return;

        refreshMeta.setDisplayName(plugin.messages.getProperty("refreshBtnName"));
        refreshMeta.setLore(Collections.singletonList(plugin.messages.getProperty("refreshBtnLore")));
        refresh.setItemMeta(refreshMeta);

        pageRightMeta.setDisplayName(plugin.messages.getProperty("pageRightBtnName"));
        pageRightMeta.setLore(Collections.singletonList(plugin.messages.getProperty("pageRightBtnLore")));
        pageRight.setItemMeta(pageRightMeta);

        pageLeftMeta.setDisplayName(plugin.messages.getProperty("pageLeftBtnName"));
        pageLeftMeta.setLore(Collections.singletonList(plugin.messages.getProperty("pageRightBtnLore")));
        pageLeft.setItemMeta(pageLeftMeta);

        backMeta.setDisplayName(plugin.messages.getProperty("backBtnName"));
        backMeta.setLore(Collections.singletonList(plugin.messages.getProperty("backBtnLore")));
        back.setItemMeta(backMeta);
    }

    public void createMain(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, plugin.messages.getProperty("timeMachineInv"));

        ItemStack backup = new ItemStack(Material.MUSIC_DISC_13);
        ItemStack restore = new ItemStack(Material.CLOCK);
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemStack deleteBackups = new ItemStack(Material.MUSIC_DISC_11);

        ItemMeta backupMeta = backup.getItemMeta();
        ItemMeta restoreMeta = restore.getItemMeta();
        ItemMeta wandMeta = wand.getItemMeta();
        ItemMeta deleteBackupsMeta = deleteBackups.getItemMeta();
        if(backupMeta == null || restoreMeta == null || wandMeta == null || deleteBackupsMeta == null)
            return;
        backupMeta.setDisplayName(plugin.messages.getProperty("backupBtnName"));
        backupMeta.setLore(Collections.singletonList(plugin.messages.getProperty("backupBtnlore")));
        backup.setItemMeta(backupMeta);

        restoreMeta.setDisplayName(plugin.messages.getProperty("restoreBtnName"));
        restore.setItemMeta(restoreMeta);

        wandMeta.setDisplayName(plugin.messages.getProperty("wandBtnName"));
        wandMeta.addEnchant(Enchantment.LUCK, 1, false);
        wandMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wand.setItemMeta(wandMeta);

        deleteBackupsMeta.setDisplayName(plugin.messages.getProperty("deleteBtnName"));
        deleteBackupsMeta.setLore(Collections.singletonList(plugin.messages.getProperty("deleteBtnlore")));
        deleteBackups.setItemMeta(deleteBackupsMeta);

        ItemStack[] items = {backup, restore, wand, deleteBackups};
        gui.setContents(items);

        player.openInventory(gui);
    }
    public void createWand(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, plugin.messages.getProperty("wandInv"));

        ItemStack give = new ItemStack(Material.BLAZE_ROD);
        ItemStack cancel = new ItemStack(Material.ROSE_RED);
        ItemStack select = new ItemStack(Material.LIME_DYE);
        ItemStack deselect = new ItemStack(Material.MAGENTA_DYE);

        ItemMeta giveMeta = give.getItemMeta();
        ItemMeta cancelMeta = cancel.getItemMeta();
        ItemMeta selectMeta = select.getItemMeta();
        ItemMeta deselectMeta = deselect.getItemMeta();
        if(giveMeta == null || cancelMeta == null || selectMeta == null || deselectMeta == null)
            return;
        giveMeta.setDisplayName(plugin.messages.getProperty("giveBtnName"));
        giveMeta.addEnchant(Enchantment.LUCK, 1, false);
        giveMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        giveMeta.setLore(Arrays.asList(plugin.messages.getProperty("giveBtnLore1"), plugin.messages.getProperty("giveBtnLore2"),plugin.messages.getProperty("giveBtnLore3"), plugin.messages.getProperty("giveBtnLore4")));
        give.setItemMeta(giveMeta);

        cancelMeta.setDisplayName(plugin.messages.getProperty("cancelBtnName"));
        cancelMeta.setLore(Arrays.asList(plugin.messages.getProperty("cancelBtnLore1"), plugin.messages.getProperty("cancelBtnLore2")));
        cancel.setItemMeta(cancelMeta);

        selectMeta.setDisplayName(plugin.messages.getProperty("selectBtnName"));
        selectMeta.setLore(Collections.singletonList(plugin.messages.getProperty("selectBtnLore")));
        select.setItemMeta(selectMeta);

        deselectMeta.setDisplayName(plugin.messages.getProperty("deselectBtnName"));
        deselectMeta.setLore(Collections.singletonList(plugin.messages.getProperty("deselectBtnLore")));
        deselect.setItemMeta(deselectMeta);

        ItemStack[] items = {give, cancel, select, deselect, blank, blank, blank, blank, back};
        gui.setContents(items);

        player.openInventory(gui);
    }
    public void createRestore(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, plugin.messages.getProperty("restoreInv"));

        ItemStack server = new ItemStack(Material.LAVA_BUCKET);
        ItemStack world = new ItemStack(Material.FIREWORK_STAR);
        ItemStack playerFile = new ItemStack(Material.PLAYER_HEAD);
        ItemStack chunk = new ItemStack(Material.GRASS_BLOCK);

        ItemMeta serverMeta = server.getItemMeta();
        ItemMeta worldMeta = world.getItemMeta();
        SkullMeta playerFileMeta = (SkullMeta)playerFile.getItemMeta();
        ItemMeta chunkMeta = chunk.getItemMeta();
        if(serverMeta == null || worldMeta == null || playerFileMeta == null || chunkMeta == null)
            return;
        serverMeta.setDisplayName(plugin.messages.getProperty("serverBtnName"));
        server.setItemMeta(serverMeta);

        worldMeta.setDisplayName(plugin.messages.getProperty("worldBtnName"));
        world.setItemMeta(worldMeta);

        playerFileMeta.setDisplayName(plugin.messages.getProperty("playerBtnName"));
        playerFileMeta.setOwningPlayer(player);
        playerFile.setItemMeta(playerFileMeta);

        chunkMeta.setDisplayName(plugin.messages.getProperty("chunksBtnName"));
        chunk.setItemMeta(chunkMeta);

        ItemStack[] items = {server, world, playerFile, chunk, blank, blank, blank, blank, back};
        gui.setContents(items);

        player.openInventory(gui);
    }
    public void createSelectPlayer(Player player, int page){
        plugin.fillOfflinePlayers();
        int size = 54;
        Inventory gui = Bukkit.createInventory(player, size, plugin.messages.getProperty("selectPlayerInv").replaceAll("%PAGE%", page + ""));
        int playersPerPage = size - 9;
        ArrayList<ItemStack> items = new ArrayList<>();
        ItemStack all = new ItemStack(Material.EMERALD);
        ItemMeta allMeta = all.getItemMeta();
        if(allMeta == null) return;
        allMeta.setDisplayName(plugin.messages.getProperty("allBtnName"));
        all.setItemMeta(allMeta);
        items.add(all);
        List<OfflinePlayer> players = objectsOnPage(page, Arrays.asList(plugin.offlinePlayers), playersPerPage);
        OfflinePlayer player1;
        boolean addRightButton = true;
        for(int i = 0; i<playersPerPage - 1; i++){
            if(i<players.size()) {
                player1 = players.get(i);
                ItemStack playerStack = new ItemStack(Material.PLAYER_HEAD);

                SkullMeta playerFileMeta = (SkullMeta) playerStack.getItemMeta();
                assert playerFileMeta != null;
                playerFileMeta.setOwningPlayer(player1);
                playerStack.setItemMeta(playerFileMeta);

                items.add(playerStack);
            }
            else{
                items.add(new ItemStack(Material.AIR));
                addRightButton = false;
            }
        }
        items.addAll(Arrays.asList(back, blank, blank, page != 1 ? pageLeft : blank, refresh, addRightButton ? pageRight : blank, blank, blank, blank));
        gui.setContents(items.toArray(new ItemStack[size]));

        player.openInventory(gui);
    }
    public void createRestorePlayer(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, plugin.messages.getProperty("restorePlayerInv"));

        ItemStack all = new ItemStack(Material.EMERALD);
        ItemStack inventory = new ItemStack(Material.CHEST);
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST);

        ItemMeta allMeta = all.getItemMeta();
        assert allMeta != null;
        allMeta.setDisplayName(plugin.messages.getProperty("allBtnName"));
        all.setItemMeta(allMeta);

        ItemMeta inventoryMeta = inventory.getItemMeta();
        assert inventoryMeta != null;
        inventoryMeta.setDisplayName(plugin.messages.getProperty("inventoryBtnName"));
        inventory.setItemMeta(inventoryMeta);

        ItemMeta enderChestMeta = enderChest.getItemMeta();
        assert enderChestMeta != null;
        enderChestMeta.setDisplayName(plugin.messages.getProperty("enderChestBtnName"));
        enderChest.setItemMeta(enderChestMeta);


        ItemStack[] items = {all, inventory, enderChest, blank, blank, blank, blank, blank, back};
        gui.setContents(items);

        player.openInventory(gui);
    }
    public void createSelectBackup(Player player, int page){
        plugin.getBackupFiles();
        Inventory gui = Bukkit.createInventory(player, 54, plugin.messages.getProperty("selectBackupInv").replaceAll("%PAGE%", page + ""));
        int filesPerPage = 54 - 9;
        ArrayList<ItemStack> items = new ArrayList<>();
        List<File> files = objectsOnPage(page, plugin.backupList, filesPerPage);

        File file;
        boolean addRightButton = true;
        for(int i = 0; i<filesPerPage; i++){
            if(i<files.size()) {
                file = files.get(i);
                ItemStack fileStack = new ItemStack(Material.MUSIC_DISC_13);

                ItemMeta fileMeta = fileStack.getItemMeta();
                assert fileMeta != null;
                fileMeta.setDisplayName(file.getName());
                fileStack.setItemMeta(fileMeta);
                items.add(fileStack);
            }
            else{
                items.add(new ItemStack(Material.AIR));
                addRightButton = false;
            }
        }
        items.addAll(Arrays.asList(back, blank, blank, page != 1 ? pageLeft : blank, refresh, addRightButton ? pageRight : blank, blank, blank, blank));
        gui.setContents(items.toArray(new ItemStack[54]));

        player.openInventory(gui);
    }
    public void createSelectWorld(Player player, int page){
        Inventory gui = Bukkit.createInventory(player, 54, plugin.messages.getProperty("selectWorldInv").replaceAll("%PAGE%", page + ""));
        int filesPerPage = 54 - 9;
        ArrayList<ItemStack> items = new ArrayList<>();

        ItemStack all = new ItemStack(Material.EMERALD);
        ItemMeta allMeta = all.getItemMeta();
        assert allMeta != null;
        allMeta.setDisplayName(plugin.messages.getProperty("allBtnName"));
        all.setItemMeta(allMeta);
        if(!args.contains("chunk"))
            items.add(all);
        List<World> worlds = objectsOnPage(page, Bukkit.getWorlds(), filesPerPage);
        World world;
        boolean addRightButton = true;
        for(int i = 0; i<filesPerPage - (!args.contains("chunk")?1:0); i++){
            if(i<worlds.size()) {
                world = worlds.get(i);
                ItemStack worldStack = new ItemStack(Material.FIREWORK_STAR);

                ItemMeta fileMeta = worldStack.getItemMeta();
                assert fileMeta != null;
                fileMeta.setDisplayName(world.getName());
                worldStack.setItemMeta(fileMeta);
                items.add(worldStack);
            }
            else{
                items.add(new ItemStack(Material.AIR));
                addRightButton = false;
            }
        }
        items.addAll(Arrays.asList(back, blank, blank, page != 1 ? pageLeft : blank, refresh, addRightButton ? pageRight : blank, blank, blank, blank));
        gui.setContents(items.toArray(new ItemStack[54]));

        player.openInventory(gui);
    }

    public <T> List<T> objectsOnPage(int page, List<T> list, int objectsPerPage){
        if(list.subList(objectsPerPage * (page - 1), list.size()).size() >= objectsPerPage)
            list = list.subList(objectsPerPage * (page - 1), objectsPerPage * page);
        else if(list.isEmpty())
            list = new ArrayList<>();
        else
            list = list.subList(objectsPerPage * (page - 1), list.size());
        return list;
    }
}
