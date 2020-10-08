package com.github.zwarunek.timemachine.util;

import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.commands.Backup;
import com.github.zwarunek.timemachine.commands.GUI;
import com.github.zwarunek.timemachine.commands.Restore;
import com.github.zwarunek.timemachine.items.ChunkWand;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeMachineCommand implements CommandExecutor {

    private final TimeMachine plugin;
    public TimeMachineCommand(final TimeMachine instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return handleCommand(sender, args);
    }
    public boolean handleCommand(CommandSender sender, String[] args){

        File backupFile;
        String world;
        if(!sender.hasPermission("timemachine")){
            sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " You do no have permission to use this command");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(ChatColor.DARK_AQUA + "---===###-Time Machine-###===---\n " + ChatColor.RESET +
                    "/tm backup : Starts a backup of the server\n" +
                    "/tm restore server <backup> : restores all server files\n" +
                    "/tm restore world <world:all> <backup> : restores selected world files\n" +
                    "/tm restore player <player:all> <backup> : restores selected player's save file\n" +
                    "/tm restore chunk <world> <x,z|x,z|...:selected> <backup> : Restores chunks to backup\n" +
                    "/tm wand : Gives player the chunk selector wand\n" +
                    "/tm wand cancel : Removes wand and currently selected chunks\n" +
                    "/tm gui : Opens the Time Machine GUI\n" +
                    "/tm saveselectedchunks : Saves selected wand chunks\n" +
                    "/tm discardsavedchunks : Deselect all wand chunks\n" +
                    "/tm deletebackup <backup> : Deletes the selected backup");
            return true;
        }
        if(args[0].equalsIgnoreCase("backup")){
            if(plugin.isBackingUp){
                sender.sendMessage(ChatColor.YELLOW + "[WARNING]" + ChatColor.DARK_AQUA + " Server is already backing up");
                return true;
            }
            try {
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + "Backup Started at " + plugin.dateFormat.format(new Date()));
                backupFile = Backup.backup(plugin, sender);

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(!plugin.isBackingUp){
                            sender.sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Server was backed up!");
                            Bukkit.getScheduler().cancelTask(Backup.taskIndex);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 20, 5);
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Backup Complete!");
                plugin.backupList.add(backupFile);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Failed to backup server. stack trace printed in console");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("restore")){
            switch(args[1].toLowerCase()){
                case "server":
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[2]);
                    if(backupFile.exists()){
                        try {
                            sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Server is restoring to " + backupFile.getName());
                            Restore.server(plugin, backupFile);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Server was restored to " + backupFile.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Server restarting in 5 seconds...");
                            plugin.restartServer();
                        }catch(Exception e){
                            sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Restore failed. stack trace printed in console");
                            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "[ERROR]" + ChatColor.DARK_AQUA + " cannot find file: " + backupFile.getName());
                    }
                    break;
                case "world":
                    if(verifyWorld(sender, world = args[2])) return true;
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[3]);
                    if(backupFile.exists()){
                        try{
                            sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Restore World: restoring " + world + " to " + backupFile.getName());
                            Restore.world(plugin, backupFile, world);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Restore World: restored " + world + " to " + backupFile.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Server restarting in 5 seconds...");
                            plugin.restartServer();
                        }catch (Exception e){
                            sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Restore failed. Stack trace printed in console");
                            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                        }
                    }
                    break;
                case "player":
                    String player = args[2];
                    String playerUUID = "";
                    String part = args[3];
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[4]);
                    boolean playerExists = false;
                    for(OfflinePlayer p : Bukkit.getOfflinePlayers())
                        if(p.getName() != null && p.getName().equalsIgnoreCase(player)){
                            playerExists = true;
                            playerUUID = p.getUniqueId().toString();
                        }

                    if(!playerExists){
                        sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Player not found");
                        return true;
                    }
                    if(backupFile.exists()){
                        try{
                            sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Restore Player: restoring " + player + " to " + backupFile.getName());
                            Restore.player(plugin, backupFile, playerUUID, part);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Restore Player: restored " + player + " to " + backupFile.getName());
                        }catch (Exception e){
                            sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Restore failed. Stack trace printed in console");
                            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                            return true;
                        }
                    }

                    break;
                case "chunk":
                    if(args.length != 5) {
                        sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Not a valid input");
                        return true;
                    }
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[4]);
                    if(!backupFile.exists()){
                        sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Backup not found");
                        return true;
                    }
                    List<Chunk> chunks;
                    if(!verifyWorld(sender, world = args[2])) return true;
                    try{
                        if(args[3].equalsIgnoreCase("selected")){
                            if(Restore.selectedChunks.isEmpty()){
                                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " There are no selected chunks");
                                return true;
                            }
                            chunks = null;
                        }

                        else {
                            String[] tempChunks = args[3].split("\\|");
                            chunks = new ArrayList<>(tempChunks.length);
                            for (int i = 0; i < tempChunks.length; i++) {
                                for (int j = 0; j < 2; j++) {
                                    chunks.add(Bukkit.getWorld(world).getChunkAt(i, j));
                                }
                            }
                        }
                        Restore.chunk(plugin, backupFile, world, chunks);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Restore Chunks: restored to " + backupFile.getName());
                        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Server restarting in 5 seconds...");
                        plugin.restartServer();
                    }catch (Exception e){
                        sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Not a valid chunk input. Must be in this format: x,z|x,z|x,z...");
                        Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                        return true;
                    }
                    break;
            }
        }
        else if(args[0].equalsIgnoreCase("wand")){
            if(args.length == 1){
                if(!(sender instanceof Player)){
                    sender.sendMessage("Only players can access that command");
                    return true;
                }
                if(plugin.chunkWand.isInUse){
                    sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Chunk wand is already in use");
                    return true;
                }
                if(((Player) sender).getInventory().firstEmpty() == -1){
                    sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Please make space in inventory");
                    return true;
                }
                ((Player) sender).getInventory().addItem(plugin.chunkWand.getChunkWand(((Player) sender)));
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Chunk wand given");
                return true;
            }
            else if(args[1].equalsIgnoreCase("cancel")){
                for(ItemStack stack : ((Player)sender).getInventory().getContents()){

                    if(stack != null && stack.hasItemMeta() && stack.getItemMeta().hasLore() && stack.getItemMeta().getLore().equals(ChunkWand.getLore())) {
                        sender.sendMessage(stack.getType().name());
                        ((Player) sender).getInventory().remove(stack);
                        plugin.chunkWand.isInUse = false;
                        plugin.chunkWand.player = null;
                        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Chunk wand has been taken");
                        return true;
                    }
                }
                return true;
            }
        }
        else if(args[0].equalsIgnoreCase("saveselectedchunks")){

            if(sender instanceof Player) {
                for(ItemStack stack : ((Player)sender).getInventory().getContents()){
                    if(stack != null && stack.hasItemMeta() && stack.getItemMeta().hasLore() && stack.getItemMeta().getLore().equals(ChunkWand.getLore())) {
                        List<Chunk> chunks = new ArrayList<>();
                        for(TMChunk tmChunk : plugin.chunkWand.getSelectedChunks()){
                            chunks.add(tmChunk.getChunk());
                        }
                        Restore.setSelectedChunks(chunks);
                        plugin.chunkWand.deselectChunks();
                        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Saved selected chunks");

                        return true;
                    }
                }
            }
            else{
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Consoles cannot use this");
                return true;
            }
            sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Not saved, you dont have a chunk wand");
            return true;
        }
        else if(args[0].equalsIgnoreCase("discardsavedchunks")){
            if(sender instanceof Player) {
                Restore.selectedChunks = new ArrayList<>();
                plugin.chunkWand.deselectChunks();
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Selected chunks were discarded");
            }
            else{
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Consoles cannot use this");
            }
            return true;
        }
        else if(args[0].equalsIgnoreCase("deletebackup")){
            if(args.length != 2){
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Invalid input");
            }
            backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[1]);
            if(!backupFile.exists()){
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Backup not found");
                return true;
            }
            try {
                FileUtils.forceDelete(backupFile);
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Backup deleted: " + backupFile.getName());
            } catch (IOException e) {
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Backup could not be deleted");

            }
            return true;
        }
        else if(args[0].equalsIgnoreCase("gui")){
            if(sender instanceof Player) {
                plugin.gui.createMain((Player) sender);
            }
            else{
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Consoles cannot access this");
            }
            return true;
        }
        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Unknown command. /tm for available commands");
        return true;
    }
    private boolean verifyWorld(CommandSender sender, String world){
        if(Bukkit.getWorld(world) == null){
            sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " World not found");
            return false;
        }
        return true;
    }
}
