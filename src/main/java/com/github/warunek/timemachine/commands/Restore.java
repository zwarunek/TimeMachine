package com.github.warunek.timemachine.commands;

import com.github.warunek.timemachine.TimeMachine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Restore {


    public static void player(TimeMachine plugin, File backup, String player, String part) {

    }
    public static void world(TimeMachine plugin, File backup, String world) {

    }
    public static void chunk(TimeMachine plugin, File backup, String world, int[][] chunks) {

    }

    public static void server(TimeMachine plugin, File backup) {
        //Kick all players
        for (Player player : Bukkit.getOnlinePlayers())
            player.kickPlayer(ChatColor.GREEN + "The server is restoring to a previous version");

        //Disable all plugins safely.
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
        for(String worldnames : names){
            File worldFile = new File(plugin.getMainDir(),worldnames);
            if(worldFile.exists())
                worldFile.delete();
        }

        //Start overriding files.
        File parentTo = plugin.getMainDir().getParentFile();
        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(backup));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                    File newFile = newFile(parentTo, zipEntry);

                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    zipEntry = zis.getNextEntry();
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
}
