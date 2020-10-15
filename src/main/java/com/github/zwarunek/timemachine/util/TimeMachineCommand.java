package com.github.zwarunek.timemachine.util;

import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.commands.Backup;
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
            sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("noPermissions"));
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(plugin.messages.getProperty("tmDesc") + "\n " +
                    plugin.messages.getProperty("backupDesc") + "\n" +
                    plugin.messages.getProperty("restoreDesc1") + "\n" +
                    plugin.messages.getProperty("restoreDesc2") + "\n" +
                    plugin.messages.getProperty("restoreDesc3") + "\n" +
                    plugin.messages.getProperty("restoreDesc4") + "\n" +
                    plugin.messages.getProperty("wandDesc1") + "\n" +
                    plugin.messages.getProperty("wandDesc2") + "\n" +
                    plugin.messages.getProperty("wandDesc3") + "\n" +
                    plugin.messages.getProperty("wandDesc4") + "\n" +
                    plugin.messages.getProperty("guiDesc") + "\n" +
                    plugin.messages.getProperty("deletebackupDesc"));
            return true;
        }
        if(args[0].equalsIgnoreCase("backup")){
            if(plugin.isBackingUp){
                sender.sendMessage(plugin.messages.getProperty("warningPrefix") + plugin.messages.getProperty("backupWarning"));
                return true;
            }
            try {
                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("backupNotification").replaceAll("%DATE%", plugin.dateFormat.format(new Date())));
                backupFile = Backup.backup(plugin, sender);

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(!plugin.isBackingUp){
                            sender.sendMessage(plugin.messages.getProperty("successPrefix") + plugin.messages.getProperty("backupSuccess"));
                            if(!sender.equals( Bukkit.getConsoleSender()))
                                Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("successPrefix") + plugin.messages.getProperty("backupSuccess"));
                            Bukkit.getScheduler().cancelTask(Backup.taskIndex);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 20, 5);
                plugin.backupList.add(backupFile);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(plugin.messages.getProperty("failedPrefix") + plugin.messages.getProperty("backupFailed"));
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("restore")){
            switch(args[1].toLowerCase()){
                case "server":
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[2]);
                    if(backupFile.exists()){
                        try {
                            sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("restoreStart").replaceAll("%FILE%", backupFile.getName()));
                            Restore.server(plugin, backupFile);
                            Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("successPrefix") + plugin.messages.getProperty("restoreStart").replaceAll("%FILE%", backupFile.getName()));
                            Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("restart"));
                            plugin.restartServer();
                        }catch(Exception e){
                            sender.sendMessage(plugin.messages.getProperty("failedPrefix") + plugin.messages.getProperty("restoreFailed"));
                            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                        }
                    }
                    else {
                        sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("fileNotFound").replaceAll("%FILE%", backupFile.getName()));
                    }
                    break;
                case "world":
                    if(!verifyWorld(sender, world = args[2]) || args.length < 4)
                        return true;
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[3]);
                    if(backupFile.exists()){
                        try{
                            sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("restoreWorldStart").replaceAll("%WORLD%", world).replaceAll("%WORLD%", backupFile.getName()));
                            Restore.world(plugin, backupFile, world);
                            Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("successPrefix") + plugin.messages.getProperty("restoreWorldFinished").replaceAll("%WORLD%", world).replaceAll("%WORLD%", backupFile.getName()));
                            Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("restart"));
                            plugin.restartServer();
                        }catch (Exception e){
                            sender.sendMessage(plugin.messages.getProperty("failedPrefix") + plugin.messages.getProperty("restoreFailed"));
                            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                        }
                    }
                    else {
                        sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("fileNotFound").replaceAll("%FILE%", backupFile.getName()));
                    }
                    break;
                case "player":
                    String player = args[2];
                    String playerUUID = "all";
                    String part = args[3];
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[4]);
                    boolean playerExists = false;
                    for(OfflinePlayer p : Bukkit.getOfflinePlayers())
                        if(p.getName() != null && p.getName().equalsIgnoreCase(player)){
                            playerExists = true;
                            playerUUID = p.getUniqueId().toString();
                        }

                    if(!playerExists && !player.equalsIgnoreCase("all")){
                        sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("playerNotFound"));
                        return true;

                    }
                    if(backupFile.exists()){
                        try{
                            Restore.player(plugin, backupFile, playerUUID, part);
                            Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("successPrefix") + plugin.messages.getProperty("restorePlayerFinished").replaceAll("%PLAYER%", player).replaceAll("%WORLD%", backupFile.getName()));
                        }catch (Exception e){
                            sender.sendMessage(plugin.messages.getProperty("failedPrefix") + plugin.messages.getProperty("restoreFailed"));
                            if(!sender.equals(Bukkit.getConsoleSender()))
                                sender.sendMessage(plugin.messages.getProperty("failedPrefix") + plugin.messages.getProperty("restoreFailed"));
                            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                            return true;
                        }
                    }
                    else {
                        sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("fileNotFound").replaceAll("%FILE%", backupFile.getName()));
                    }
                    break;
                case "chunk":
                    if(args.length != 5) {
                        sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("invalidInput"));
                        return true;
                    }
                    backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[4]);
                    if(!backupFile.exists()){
                        sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("fileNotFound").replaceAll("%FILE%", backupFile.getName()));
                        return true;
                    }
                    List<Chunk> chunks;
                    if(!verifyWorld(sender, world = args[2])) return true;
                    try{
                        if(args[3].equalsIgnoreCase("selected")){
                            if(Restore.selectedChunks.isEmpty()){
                                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("noSelectedChunks"));
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
                        Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("successPrefix") + plugin.messages.getProperty("restoreChunks").replaceAll("%FILE%", backupFile.getName()));
                        Bukkit.getConsoleSender().sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("restart"));
                        plugin.restartServer();
                    }catch (Exception e){
                        sender.sendMessage(plugin.messages.getProperty("failedPrefix") + plugin.messages.getProperty("restoreFailed"));
                        Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                        return true;
                    }
                    break;
            }
        }
        else if(args[0].equalsIgnoreCase("wand")){
            if(!(sender instanceof Player)){
                sender.sendMessage(plugin.messages.getProperty("onlyPlayers"));
                return true;
            }
            if(args[1].equalsIgnoreCase("give")){
                if(plugin.chunkWand.isInUse){
                    sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("wandInUse") );
                    return true;
                }
                if(((Player) sender).getInventory().firstEmpty() == -1){
                    sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("noInvSpace") );
                    return true;
                }
                ((Player) sender).getInventory().addItem(plugin.chunkWand.getChunkWand(((Player) sender)));
                return true;
            }
            else if(args[1].equalsIgnoreCase("cancel")){
                for(ItemStack stack : ((Player)sender).getInventory().getContents()){
                    if(stack != null && stack.isSimilar(plugin.chunkWand.chunkWand)) {
                        ((Player) sender).getInventory().remove(stack);
                        plugin.chunkWand.deselectChunks();
                        plugin.chunkWand.isInUse = false;
                        plugin.chunkWand.player = null;
                        sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("wandCancel"));
                        return true;
                    }
                }
                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("noWand"));
                return true;
            }else if(args[1].equalsIgnoreCase("selectchunks")){

                for(ItemStack stack : ((Player)sender).getInventory().getContents()){
                    if(stack != null && stack.isSimilar(plugin.chunkWand.chunkWand)) {
                        List<Chunk> chunks = new ArrayList<>();
                        for(TMChunk tmChunk : plugin.chunkWand.getSelectedChunks()){
                            chunks.add(tmChunk.getChunk());
                        }
                        Restore.setSelectedChunks(chunks);
                        plugin.chunkWand.deselectChunks();
                        sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("savedChunks"));

                        return true;
                    }
                }
                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("noWand"));
                return true;
            }
            else if(args[1].equalsIgnoreCase("deselectchunks")){
                for(ItemStack stack : ((Player)sender).getInventory().getContents()){
                    if(stack != null && stack.isSimilar(plugin.chunkWand.chunkWand)) {
                        Restore.selectedChunks = new ArrayList<>();
                        plugin.chunkWand.deselectChunks();
                        sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("savedDiscarded"));
                        return true;
                    }
                }
                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("noWand"));
                return true;
            }
        }
        else if(args[0].equalsIgnoreCase("deletebackup")){
            if(args.length != 2){
                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("invalidInput"));
            }
            backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[1]);
            if(!backupFile.exists()){
                sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("fileNotFound").replaceAll("%FILE%", backupFile.getName()));
                return true;
            }
            try {
                FileUtils.forceDelete(backupFile);
                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("backupDeleted").replaceAll("%FILE%", backupFile.getName()));
                plugin.getBackupFiles();
            } catch (IOException e) {
                sender.sendMessage(plugin.messages.getProperty("failedPrefix") + plugin.messages.getProperty("backupDeleteFailed"));

            }
            return true;
        }
        else if(args[0].equalsIgnoreCase("gui")){
            if(sender instanceof Player) {
                plugin.gui.createMain((Player) sender);
            }
            else{
                sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("onlyPlayers"));
            }
            return true;
        }
        sender.sendMessage(plugin.messages.getProperty("tmPrefix") + plugin.messages.getProperty("unknownCommand"));
        return true;
    }
    private boolean verifyWorld(CommandSender sender, String world){
        if(Bukkit.getWorld(world) == null){
            sender.sendMessage(plugin.messages.getProperty("errorPrefix") + plugin.messages.getProperty("worldNotFound"));
            return false;
        }
        return true;
    }
}
