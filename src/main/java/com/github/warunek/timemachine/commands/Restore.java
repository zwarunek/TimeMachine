package com.github.warunek.timemachine.commands;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.warunek.timemachine.TimeMachine;
import com.github.warunek.timemachine.util.RegionFile;
import com.sun.istack.internal.Nullable;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Restore {


    public static void player(TimeMachine plugin, File backup, String player, String part) throws IOException, InterruptedException {
        prepareServer(plugin, player, "Restoring your playersave to a previous backup");
        String backupDir = backup.getParent();
        switch(part.toLowerCase()){
            case "inventory":
                part = "Inventory";
                break;
            case "enderchest":
                part = "EnderItems";
                break;
        }
        if(player.equalsIgnoreCase("all")){
            if(part.equalsIgnoreCase("all")) {
                String dest = plugin.mainDir.getParent();
                String folder = (plugin.mainDir.getName() + "/world/playerdata/");
                String del = plugin.mainDir.getAbsolutePath() + File.separator + "world" + File.separator + "playerdata";
                List<String> filter = new ArrayList<String>();
                unzip(backup.getAbsolutePath(), dest, folder, del, filter);
                folder = (plugin.mainDir.getName() + "/world/advancements/");
                del = plugin.mainDir.getAbsolutePath() + File.separator + "world" + File.separator + "advancements";
                unzip(backup.getAbsolutePath(), dest, folder, del, filter);
                folder = (plugin.mainDir.getName() + "/world/stats/");
                del = plugin.mainDir.getAbsolutePath() + File.separator + "world" + File.separator + "stats";
                unzip(backup.getAbsolutePath(), dest, folder, del, filter);
            }
            else{
                ZipFile zip = new ZipFile(backup);
                zip.extractFile(plugin.mainDir.getName() + "/world/playerdata/", backupDir, "TempPlayerFiles");

                FilenameFilter fileNameFilter = (dir, name) -> name.endsWith(".dat");

                File currentPlayerdata = new File(plugin.mainDir + File.separator + "world" + File.separator + "playerdata");
                String[] currentPlayers = Objects.requireNonNull(currentPlayerdata.list(fileNameFilter));

                File backedPlayerdata = new File(backupDir + File.separator + "TempPlayerFiles");
                List<String> backedPlayers = Arrays.asList(Objects.requireNonNull(backedPlayerdata.list(fileNameFilter)));

                for(String playerFile : currentPlayers){
                    String uuid = new File(playerFile).getName();
                    CompoundTag currentTag = NBTIO.readFile(plugin.mainDir + File.separator + "world" + File.separator + "playerdata" + File.separator + uuid);
                    currentTag.put(new ListTag(part));

                    if(backedPlayers.contains(uuid)) {

                        CompoundTag backedTag = NBTIO.readFile(backupDir + File.separator + "TempPlayerFiles" + File.separator + uuid);
                        currentTag.put(backedTag.get(part));
                        NBTIO.writeFile(currentTag, plugin.mainDir + File.separator + "world" + File.separator + "playerdata" + File.separator + uuid);
                    }
                }
                FileUtils.forceDelete(new File(backupDir + File.separator + "TempPlayerFiles"));
            }
        }
        else{
            if(part.equalsIgnoreCase("all")) {
                String dest = plugin.mainDir.getParent();
                String folder = (plugin.mainDir.getName() + "/world/playerdata/" + player + ".dat");
                List<String> filter = new ArrayList<String>();
                unzip(backup.getAbsolutePath(), dest, folder, null, filter);
                folder = (plugin.mainDir.getName() + "/world/playerdata/" + player + ".dat_old");
                unzip(backup.getAbsolutePath(), dest, folder, null, filter);
                folder = (plugin.mainDir.getName() + "/world/advancements/" + player + ".json");
                unzip(backup.getAbsolutePath(), dest, folder, null, filter);
            }
            else{
                ZipFile zip = new ZipFile(backup);
                zip.extractFile(plugin.mainDir.getName() + "/world/playerdata/" + player + ".dat", backupDir, "TempPlayerFile.dat");
                CompoundTag backedTag = NBTIO.readFile(backupDir + File.separator + "TempPlayerFile.dat");
                CompoundTag currentTag = NBTIO.readFile(plugin.mainDir + File.separator + "world" + File.separator + "playerdata" + File.separator + player +".dat");
                currentTag.put(backedTag.get(part));
                NBTIO.writeFile(currentTag, plugin.mainDir + File.separator + "world" + File.separator + "playerdata" + File.separator + player +".dat");
                FileUtils.forceDelete(new File(backupDir + File.separator + "TempPlayerFile.dat"));
            }
        }
    }
    public static void world(TimeMachine plugin, File backup, String worlds) throws IOException, InterruptedException {
        //Kick all players
        try {
            prepareServer(plugin, "all", "The server is restoring to a previous backup");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        List<String> worldNameList = new ArrayList<>();
        if(worlds.equalsIgnoreCase("all"))
            for(World world : Bukkit.getWorlds()){
                worldNameList.add(world.getWorldFolder().getName());
            }

        else
            worldNameList = Arrays.asList(worlds);

        for(String worldName : worldNameList) {
            String dest = plugin.mainDir.getParent();
            String folder = (plugin.mainDir.getName() + "/" + worldName + "/");
            String del = plugin.mainDir.getAbsolutePath() + File.separator + worldName;
            List<String> filter = plugin.restorePlayerWithWorld ? new ArrayList<String>() : Arrays.asList("playerdata", "advancements");
            unzip(backup.getAbsolutePath(), dest, folder, del, filter);
        }
    }
    public static void chunk(TimeMachine plugin, File backup, String world, int[][] chunks) throws IOException {

        prepareServer(plugin, "all", "The server is restoring to a previous backup");
        String backupDir = backup.getParent();
        for(int[] chunk : chunks) {
            String regionFileName = "r." + (chunk[0] >> 5) + "." + (chunk[1] >> 5) + ".mca";
            File currentFile;
            int x = chunk[0] % (32);
            int y = chunk[1] % (32);

            currentFile = new File(plugin.mainDir.getAbsolutePath() + File.separator + world + File.separator + "region" + File.separator + regionFileName);
            if(!currentFile.exists()){
                System.out.println("Handle this exception");
                return;
            }

            ZipFile zip = new ZipFile(backup);
            zip.setRunInThread(false);
            try {
                zip.extractFile(plugin.mainDir.getName() + "/"+world+"/region/" + regionFileName, backupDir, regionFileName);
            }catch (ZipException e){
//                HANDLE ERROR: Chunk File not backed up
                System.out.println("HANDLE ERROR: Chunk File not backed up");
                return;
            }
            File backedFile = new File(backupDir + File.separator + regionFileName);
            DataOutputStream stream = new RegionFile(currentFile).getChunkDataOutputStream((x<0?x+32:x),(y<0?y+32:y));
            DataInputStream in = new RegionFile(backedFile).getChunkDataInputStream((x<0?x+32:x),(y<0?y+32:y));

            Tag tag =  NBTIO.readTag((InputStream) in);
            NBTIO.writeTag((OutputStream) stream, tag);
            stream.flush();
            stream.close();

            in.close();
            FileUtils.forceDelete(new File(backupDir + File.separator + regionFileName));
        }
    }
    public static void server(TimeMachine plugin, File backup) throws IOException, InterruptedException {
        //Kick all players
        prepareServer(plugin, "all", "The server is restoring to a previous backup");

        String dest = plugin.mainDir.getParent();
        List<String> filter = new ArrayList<>();
        unzip(backup.getAbsolutePath(), dest, null, null, filter);
    }
    public static void unzip(String file, String dest, @Nullable String folder, @Nullable String del, List<String> filter) throws IOException, InterruptedException {
        File tempZip = new File(new File(file).getParent() + File.separator + "TEMP.zip");
        FileUtils.copyFile(new File(file), tempZip);
        FilenameFilter test = new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return !filter.contains(name);
            }
        };
        if(del!=null) {
            for (File file1 : new File(del).listFiles(test)) {
                FileUtils.forceDelete(file1);
            }
        }
        ZipFile zipFile = new ZipFile(tempZip);

        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
        for(String excludedFolder : filter) {
            System.out.println(folder + excludedFolder + "/");
            zipFile.removeFile(folder + excludedFolder + "/");
        }

        zipFile.setRunInThread(true);
        if(folder == null)
            zipFile.extractAll(dest);
        else
            zipFile.extractFile(folder, dest);
        while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {

            Thread.sleep(100);
        }
        FileUtils.forceDelete(tempZip);
    }
    private static void prepareServer(TimeMachine plugin, String playerName, String message){
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isSame = player.getDisplayName().equals(playerName);
            if (isSame || playerName.equalsIgnoreCase("all")) {
                player.kickPlayer(ChatColor.GREEN + message);
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

        for (World w : Bukkit.getWorlds()) {
            w.save();
            if (w.isAutoSave()) {
                w.setAutoSave(false);
            }
            for (Chunk c : w.getLoadedChunks()) {

                c.unload(false);
            }
            Bukkit.unloadWorld(w, true);
        }
    }
//    public static void overrideFiles(TimeMachine plugin, File backup, String restoring, String world){
//
//        File parentTo = plugin.getMainDir().getParentFile();
//        try {
//            byte[] buffer = new byte[1024];
//            ZipInputStream zis = new ZipInputStream(new FileInputStream(backup));
//            ZipEntry zipEntry = zis.getNextEntry();
//            while (zipEntry != null) {
//                File newFile = newFile(parentTo, zipEntry);
//                String [] folders = zipEntry.getName().split(Pattern.quote(File.separator));
//                boolean restoreFile = false;
//                boolean restorePlayerData;
//
//                try{
//
//                    switch(restoring.toLowerCase()){
//                        case "server":
//                            restoreFile = true;
//                            break;
//                        case "world":
//                            boolean allWorlds = world.contentEquals("all");
//                            restorePlayerData = plugin.getConfig().getBoolean("restorePlayerWithWorld");
//                            restoreFile = (restorePlayerData || folders[2].equalsIgnoreCase("playerdata"))
//                                    && !allWorlds ? folders[1].equalsIgnoreCase(world)
//                                    : ((folders[1].equalsIgnoreCase("world")
//                                    || (folders[1].equalsIgnoreCase("world_nether")
//                                    || (folders[1].equalsIgnoreCase("world_the_end")))));
//                            break;
////                        case "player":
////                            OfflinePlayer[] players = ;
////                            for(OfflinePlayer player: Bukkit.getOfflinePlayers()){
////                                player.
////                            }
////                            break;
//                    }
//                }catch (IndexOutOfBoundsException e){}
//
//                if(restoreFile) {
//                    FileOutputStream fos = new FileOutputStream(newFile);
//                    try {
//                        int len;
//                        while ((len = zis.read(buffer)) > 0) {
//                            fos.write(buffer, 0, len);
//                        }
//                        fos.close();
//
//                        zipEntry = zis.getNextEntry();
//                    } catch (EOFException e) {
//                        zipEntry = null;
//                        fos.close();
//                    }
//                }else{
//                    zipEntry = zis.getNextEntry();
//                }
//            }
//            zis.closeEntry();
//            zis.close();
//        } catch (Exception e4) {
//            e4.printStackTrace();
//        }
//    }
//
//    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
//        File destFile = new File(destinationDir, zipEntry.getName());
//
//        String destDirPath = destinationDir.getCanonicalPath();
//        String destFilePath = destFile.getCanonicalPath();
//
//        if (!destFilePath.startsWith(destDirPath + File.separator)) {
//            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
//        }
//
//        return destFile;
//    }

}
