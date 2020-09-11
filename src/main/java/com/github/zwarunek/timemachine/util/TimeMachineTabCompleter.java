package com.github.zwarunek.timemachine.util;

import com.github.zwarunek.timemachine.TimeMachine;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TimeMachineTabCompleter implements TabCompleter {

    final TimeMachine plugin;

    public TimeMachineTabCompleter(final TimeMachine instance) {
        plugin = instance;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> list = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        switch (args.length) {
            case 1:
                commands = Arrays.asList("backup", "restore", "deletebackup", "wand", "saveselectedchunks", "discardsavedchunks", "gui");
                for (String f : commands)
                    if (f.toLowerCase().startsWith(args[0].toLowerCase()))
                        list.add(f);
                return list;
            case 2:
                if (args[0].equalsIgnoreCase("restore")) {
                    commands = Arrays.asList("server", "world", "player", "chunk");
                    for (String f : commands)
                        if (f.toLowerCase().startsWith(args[1].toLowerCase()))
                            list.add(f);
                    return list;
                } else if (args[0].equalsIgnoreCase("deletebackup")
                        && plugin.backups.listFiles() != null) {
                    return getBackupFiles(list, args[1]);
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("restore")) {
                    switch (args[1].toLowerCase()) {
                        case "server":
                            getBackupFiles(list, args[2]);
                            break;
                        case "world":
                            list.add("all");
                        case "chunk":
                            for(World world : Bukkit.getWorlds()){
                                commands.add(world.getName());
                            }
                            for (String f : commands) {
                                if (f.toLowerCase().startsWith(args[2].toLowerCase()))
                                    list.add(f);
                            }
                            break;
                        case "player":
                            if ("all_players".startsWith(args[2].toLowerCase()))
                                list.add("all_players");

                            for (OfflinePlayer player : plugin.offlinePlayers)
                                if (player.getName() != null &&
                                        player.getName().startsWith(args[2].toLowerCase())){
                                    list.add(player.getName());
                                }
                            break;
                    }
                    return list;
                }
                break;
            case 4:

                if (args[0].equalsIgnoreCase("restore")) {
                    if (args[1].equalsIgnoreCase("world") && args[2] != null) {
                        getBackupFiles(list, args[3]);
                    }
                    else if (args[1].equalsIgnoreCase("player") && args[2] != null) {
                        commands = Arrays.asList("all", "inventory", "enderchest");
                        for (String f : commands) {
                            if (f.toLowerCase().startsWith(args[3].toLowerCase()))
                                list.add(f);
                        }
                    }
                    else if(args[1].equalsIgnoreCase("chunk") && args[2] != null && sender instanceof Player){
                        Chunk chunk = ((Player)sender).getLocation().getChunk();
                        String temp = chunk.getX() + "," + chunk.getZ();
                        if(temp.startsWith(args[3].toLowerCase()))
                            list.add(temp);
                    }
                    return list;
                }
                break;
            case 5:
                if (args[0].equalsIgnoreCase("restore")
                        && (args[1].equalsIgnoreCase("player")
                        || args[1].equalsIgnoreCase("chunk"))
                        && args[2] != null && args[3]!= null) {
                    return getBackupFiles(list, args[4]);
                }
                break;
            case 6:
                break;
        }
        return null;
    }

    private List<String> getBackupFiles(List<String> list, String arg) {

        if (plugin.backups.listFiles() != null) {
            for (File f : Objects.requireNonNull(plugin.backups.listFiles())) {
                if (f.getName().toLowerCase().startsWith(arg.toLowerCase()))
                    list.add(f.getName());
            }
        }
        return list;
    }
}
