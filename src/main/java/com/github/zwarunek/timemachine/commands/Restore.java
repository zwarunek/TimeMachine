package com.github.zwarunek.timemachine.commands;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.zwarunek.timemachine.TimeMachine;
import com.github.zwarunek.timemachine.util.RegionFile;
import com.sun.istack.internal.Nullable;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;


public class Restore {

    public static List<Chunk> selectedChunks = new ArrayList<>();


    public static void player(TimeMachine plugin, File backup, String player, String part) throws IOException, InterruptedException {
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
            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                onlinePlayer.kickPlayer(ChatColor.GREEN + "Restoring your playersave to a previous backup");
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
            Player playerObject = Bukkit.getPlayer(UUID.fromString(player));
            assert playerObject != null;
            playerObject.kickPlayer("Restoring your playersave to a previous backup");
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

        try {
            prepareServer(plugin);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        List<String> worldNameList = new ArrayList<>();
        if(worlds.equalsIgnoreCase("all"))
            for(World world : Bukkit.getWorlds()){
                worldNameList.add(world.getWorldFolder().getName());
            }
        else
            worldNameList = Collections.singletonList(worlds);

        for(String worldName : worldNameList) {
            String dest = plugin.mainDir.getParent();
            String folder = (plugin.mainDir.getName() + "/" + worldName + "/");
            String del = plugin.mainDir.getAbsolutePath() + File.separator + worldName;
            List<String> filter = plugin.restorePlayerWithWorld ? new ArrayList<>() : Arrays.asList("playerdata", "advancements");
            unzip(backup.getAbsolutePath(), dest, folder, del, filter);
        }
    }
    public static void chunk(TimeMachine plugin, File backup, String world, @Nullable List<Chunk> chunkInputs) throws IOException {
        List<Chunk> chunks = chunkInputs;
        if(chunks == null){
            chunks = selectedChunks;
        }

        prepareServer(plugin);
        String backupDir = backup.getParent();
        for(Chunk chunk : chunks) {
            String regionFileName = "r." + (chunk.getX() >> 5) + "." + (chunk.getZ() >> 5) + ".mca";
            File currentFile;
            int x = chunk.getX() % (32);
            int y = chunk.getZ() % (32);

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
        prepareServer(plugin);

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
    private static void prepareServer(TimeMachine plugin){
        for (Player player : Bukkit.getOnlinePlayers())
            player.kickPlayer(ChatColor.GREEN + "The server is restoring to a previous backup");

        for (Plugin p : Bukkit.getPluginManager().getPlugins())
            if (p != plugin)
                try {
                    Bukkit.getPluginManager().disablePlugin(p);
                } catch (Exception e) {
                    e.printStackTrace();
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
    public static void setSelectedChunks(List<Chunk> chunks){
        selectedChunks = chunks;
    }
}
