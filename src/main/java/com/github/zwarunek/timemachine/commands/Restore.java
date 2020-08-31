package com.github.zwarunek.timemachine.commands;

import com.github.zwarunek.timemachine.TimeMachine;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Restore {


    public static void player(TimeMachine plugin, File backup, String player, String part) {

    }
    public static void world(TimeMachine plugin, File backup, String worlds) {
        //Kick all players
        prepareServer(plugin, "all", "The server is restoring to a previous backup");

        for(World world : Bukkit.getWorlds()){
            String worldnames = world.getName();
            File worldFile = new File(plugin.getMainDir(),worldnames);
            Bukkit.getConsoleSender().sendMessage(worldFile.getAbsolutePath());
            Bukkit.getConsoleSender().sendMessage(worlds);
            if(worldFile.exists() && (worldnames.equalsIgnoreCase(worlds) || worlds.equalsIgnoreCase("all")))
                worldFile.delete();
        }

        //Start overriding files.
        overrideFiles(plugin, backup, "world", worlds);
    }
    public static void chunk(TimeMachine plugin, File backup, String world, int[][] chunks) {

    }

    public static void server(TimeMachine plugin, File backup) {
        //Kick all players
        prepareServer(plugin, "all", "The server is restoring to a previous backup");

        for(World world : Bukkit.getWorlds()){
            String worldnames = world.getName();
            File worldFile = new File(plugin.getMainDir(),worldnames);
            if(worldFile.exists())
                worldFile.delete();
        }

        //Start overriding files.
        overrideFiles(plugin, backup, "server", null);
    }

    public static void overrideFiles(TimeMachine plugin, File backup, String restoring, String world){

        File parentTo = plugin.getMainDir().getParentFile();
        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(backup));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(parentTo, zipEntry);
                String [] folders = zipEntry.getName().split(Pattern.quote(File.separator));
                boolean restoreFile = false;
                boolean restorePlayerData;

                try{

                    switch(restoring.toLowerCase()){
                        case "server":
                            restoreFile = true;
                            break;
                        case "world":
                            boolean allWorlds = world.contentEquals("all");
                            restorePlayerData = plugin.getConfig().getBoolean("restorePlayerWithWorld");
                            restoreFile = (restorePlayerData || folders[2].equalsIgnoreCase("playerdata"))
                                    && !allWorlds ? folders[1].equalsIgnoreCase(world)
                                    : ((folders[1].equalsIgnoreCase("world")
                                    || (folders[1].equalsIgnoreCase("world_nether")
                                    || (folders[1].equalsIgnoreCase("world_the_end")))));
                            break;
//                        case "player":
//                            OfflinePlayer[] players = ;
//                            for(OfflinePlayer player: Bukkit.getOfflinePlayers()){
//                                player.
//                            }
//                            break;
                    }
                }catch (IndexOutOfBoundsException e){}

                if(restoreFile) {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    try {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();

                        zipEntry = zis.getNextEntry();
                    } catch (EOFException e) {
                        zipEntry = null;
                        fos.close();
                    }
                }else{
                    zipEntry = zis.getNextEntry();
                }
            }
            zis.closeEntry();
            zis.close();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private static void prepareServer(TimeMachine plugin, String playerName, String message){
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isSame = player.getDisplayName().equals(playerName);
            if (isSame || playerName.equalsIgnoreCase("all")) {
                player.kickPlayer(ChatColor.GREEN + message);
            }
            if(isSame && !playerName.equalsIgnoreCase("all")){
                return;
            }
        }
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            if (p != plugin) {
                try {
                    Bukkit.getPluginManager().disablePlugin(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Unload all worlds.
        List<String> names = new ArrayList<>();
        for (World w : Bukkit.getWorlds()) {
            for (Chunk c : w.getLoadedChunks()) {

                c.unload(false);
            }
            names.add(w.getName());
            Bukkit.unloadWorld(w, true);
        }
    }
}
