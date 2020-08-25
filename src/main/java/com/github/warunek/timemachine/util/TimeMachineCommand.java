package com.github.warunek.timemachine.util;

import com.github.warunek.timemachine.TimeMachine;
import com.github.warunek.timemachine.commands.Backup;
import com.github.warunek.timemachine.commands.Restore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.Date;

public class TimeMachineCommand implements CommandExecutor {

    private final TimeMachine plugin;
    public TimeMachineCommand(final TimeMachine instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File backupFile;
        if(!sender.hasPermission("timemachine")){
            sender.sendMessage(ChatColor.DARK_PURPLE + "You do not have permission to use this command.");

            return true;
        }
        if(args.length == 0){
            sender.sendMessage(ChatColor.AQUA + "---===###-Time Machine-###===---\n " + ChatColor.RESET +
                    "/tm backup : Starts a backup of the server\n" +
                    "/tm restore server <backup> : restores all server files\n" +
                    "/tm restore world <world:all> <backup> : restores selected world files\n" +
                    "/tm restore player <player:all> <backup> : restores selected player's save file\n" + ChatColor.GRAY +
                    "/tm restore pluginconfig <plugin:all> <backup> : Restores plugin files of selected plugin" +
                    "/tm restoreWorld <backup> <(optional)x:y>: Restores a world save or individual chunks to a previous backup" +
                    "/tm disableAutoSaver : Disables the autosaver");
            return true;
        }
        if(args[0].equalsIgnoreCase("backup")){
            if(plugin.isBackingUp){
                sender.sendMessage(ChatColor.YELLOW + "[WARNING]" + ChatColor.DARK_AQUA + " Server is already backing up");
                return true;
            }
            try {
                sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + "Backup Started at " + plugin.dateFormat.format(new Date()));
                plugin.isBackingUp = true;
                Backup.backup(plugin, sender);

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
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Failed to backup server. stack trace printed in console");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("restore")){
//            if(args.length != 2){
//                sender.sendMessage(ChatColor.DARK_AQUA + "Please give the name of the backup that you want to be restored");
//                return true;
//            }
            if(args[1].equalsIgnoreCase("server")){
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
            }
            else if(args[1].equalsIgnoreCase("world")){
                String world = args[2];
                switch (world){
                    case "overworld":
                        world = "world";
                        break;
                    case "the_nether":
                        world = "world_nether";
                        break;
                    case "the_end":
                        world = "world_end";
                        break;
                }
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

            }
            else if(args[1].equalsIgnoreCase("player")){
                String player = args[2];
                String part = args[3];
                backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[4]);
                if(backupFile.exists()){
                    try{
                        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Restore Player: restoring " + player + " to " + backupFile.getName());
                        Restore.player(plugin, backupFile, player, part);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Restore Player: restored " + player + " to " + backupFile.getName());
                        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Server restarting in 5 seconds...");
                        plugin.restartServer();
                    }catch (Exception e){
                        sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Restore failed. Stack trace printed in console");
                        Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                    }
                }

            }
            else if(args[1].equalsIgnoreCase("chunk")){
                String world = args[2];
                int[][] chunks;
                String[] tempChunks = args[3].split("\\|");
                chunks = new int[tempChunks.length][2];
                for(int i = 0; i < tempChunks.length; i++){
                    String[] temp = tempChunks[i].split(",");
                    for(int j = 0; j < 2; j++){
                        try{
                            chunks[i][j] = Integer.parseInt(temp[j]);
                        }catch (NumberFormatException e){
                            sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Restore failed. Stack trace printed in console");
                            Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
                        }
                    }
                    sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " " + chunks[i][0] + "," + chunks[i][1]);
                }


//                backupFile = new File(plugin.backups.getAbsolutePath() + File.separator + args[4]);
//                if(backupFile.exists()){
//                    try{
//                        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Restore Chunk: restoring chunks in " + world + " to " + backupFile.getName());
//                        Restore.chunk(plugin, backupFile, world, );
//                        sender.sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Restore Chunk: restored chunks in " + world + " to " + backupFile.getName());
//                        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Server restarting in 5 seconds...");
//                        plugin.restartServer();
//                    }catch (Exception e){
//                        sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Restore failed. Stack trace printed in console");
//                        Bukkit.getServer().getConsoleSender().sendMessage(e.getMessage());
//                    }
//                }

            }
            return true;
        }
        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Unknown command. /tm for available commands");
        return true;
    }
}
