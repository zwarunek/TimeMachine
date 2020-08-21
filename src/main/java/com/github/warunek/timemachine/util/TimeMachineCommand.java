package com.github.warunek.timemachine.util;

import com.github.warunek.timemachine.TimeMachine;
import com.github.warunek.timemachine.commands.Backup;
import com.github.warunek.timemachine.commands.Restore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

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
            sender.sendMessage(ChatColor.AQUA + "---===###-Time Machine-###===---");
            sender.sendMessage("/tm backup : Starts a backup of the server");
            sender.sendMessage("/tm restoreAll <backup> : Restores server to a previous backup almost instantly");
            sender.sendMessage("/tm restoreWorld <backup> <(optional)x:y>: Restores a world save or individual chunks to a previous backup");
            sender.sendMessage("/tm enableAutoSaver [1H,6H,1D,7D] : Configure how long it takes to autosave");
            sender.sendMessage("/tm disableAutoSaver : Disables the autosaver");
            return true;
        }
        if(args[0].equalsIgnoreCase("backup")){
            if(plugin.isBackingUp){
                sender.sendMessage(ChatColor.YELLOW + "[WARNING]" + ChatColor.DARK_AQUA + " Server is already backing up");
                return true;
            }
            try {
                sender.sendMessage(ChatColor.DARK_AQUA + "Backup Started at " + plugin.dateFormat.format(new Date()));
                plugin.isBackingUp = true;
                new Backup(plugin);
                sender.sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Server was backed up");
                plugin.isBackingUp = false;
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
                        sender.sendMessage(ChatColor.DARK_AQUA + " Server is restoring to " + backupFile.getName());
                        Restore.server(plugin, backupFile);
                        sender.sendMessage(ChatColor.GREEN + "[SUCCESS]" + ChatColor.DARK_AQUA + " Server was restored to " + backupFile.getName());
                        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Server restarting in 5 seconds...");
                        plugin.restartServer();
                    }catch(Exception e){
                        sender.sendMessage(ChatColor.RED + "[FAILED]" + ChatColor.DARK_AQUA + " Restore failed. stack trace printed in console");
                        Log.error(e.getMessage());
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "[ERROR]" + ChatColor.DARK_AQUA + " cannot find file: " + backupFile.getName());
                }
            }
            else if(args[1].equalsIgnoreCase("world")){

            }
            else if(args[1].equalsIgnoreCase("player")){

            }
            else if(args[1].equalsIgnoreCase("chunk")){

            }
            return true;
        }
        sender.sendMessage(ChatColor.AQUA + "[Time Machine]" + ChatColor.DARK_AQUA + " Unknown command. /tm for available commands");
        return true;
    }
}
