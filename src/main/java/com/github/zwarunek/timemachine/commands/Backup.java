package com.github.zwarunek.timemachine.commands;

import com.github.zwarunek.timemachine.TimeMachine;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ExcludeFileFilter;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Backup {

    static TimeMachine plugin;
    static CommandSender sender;
    static int temp = 0;
    static public int taskIndex;

    public static void backup(final TimeMachine instance, CommandSender sender) throws Exception {
        plugin = instance;
        Backup.sender = sender;
        List<World> autosave = new ArrayList<>();
        Bukkit.savePlayers();
        for (World loaded : Bukkit.getWorlds()) {
            try {
                loaded.save();
                if (loaded.isAutoSave()) {
                    autosave.add(loaded);
                    loaded.setAutoSave(false);
                }

            } catch (Exception e) {}
        }
        Player player = null;
        BossBar bar = Bukkit.createBossBar(ChatColor.DARK_AQUA + "Backing Up Server", BarColor.GREEN, BarStyle.SOLID);
        if(sender instanceof Player) {
            player = (Player) sender;
            bar.addPlayer(player);
            bar.setVisible(true);
        }
        long time = System.currentTimeMillis();
        Date d = new Date(time);
        ZipFile zipFile = new ZipFile(plugin.backups.getAbsolutePath() + File.separator + plugin.backupNameFormat.replaceAll("%date%", plugin.dateFormat.format(d)) + ".zip");
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        ExcludeFileFilter exclude = file -> file.getName().equalsIgnoreCase("backups");
        ZipParameters zipParam = new ZipParameters();
        zipParam.setExcludeFileFilter(exclude);
        zipFile.setRunInThread(true);
        bar.setProgress(0);
        try {
            zipFile.addFolder(new File(plugin.mainDir.getAbsolutePath()), zipParam);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        taskIndex = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                    bar.setProgress(Math.min(((double) progressMonitor.getPercentDone()) / 100, 1.0));
                    bar.setTitle(ChatColor.DARK_AQUA + "Backing Up Server: " + ChatColor.GOLD + progressMonitor.getPercentDone() + "%");
                }
                else{
                    plugin.isBackingUp = false;
                    bar.removeAll();
                }
            }
        },0, 5);
    }

    private static void findSrcFiles(String path, String srcFile, ZipOutputStream zip) {
        try {
            File folder = new File(srcFile);

            if (folder.isDirectory()) {
                findSrcFolders(path, srcFile, zip);
            } else {
                if (folder.getName().endsWith("jar")) {
                    if (path.contains("plugins") && (!plugin.savePluginJars) || (!path.contains("plugins") && (!plugin.saveServerJar))) {
                        return;
                    }
                }
                String substring = path.substring(path.lastIndexOf(File.separator) + 1);
                if((plugin.exempt.contains(substring)) || "backups".equalsIgnoreCase(substring)){
                    return;
                }

                byte[] buf = new byte['?'];

                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + File.separator + folder.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
                in.close();
            }

        }catch (Exception e){
        }
    }
    private static void findSrcFolders(String path, String srcFolder, ZipOutputStream zip) {

        try {
            File folder = new File(srcFolder);
            String[] arrayOfString;
            int j = (arrayOfString = folder.list()).length;
            for (int i = 0; i < j; i++) {
                if((!path.toLowerCase().contains("backups")) && (!plugin.exempt.contains(path.substring(path.lastIndexOf(File.separator) + 1)))) {
                    String fileName = arrayOfString[i];
                    if (path.equals("")) {
                        findSrcFiles(folder.getName(), srcFolder + File.separator + fileName, zip);
                    } else {
                        findSrcFiles(path + File.separator + folder.getName(), srcFolder + File.separator + fileName, zip);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private static void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);


        zip.setLevel(9);

        findSrcFolders("", srcFolder, zip);
        zip.flush();
        zip.close();
    }
}
