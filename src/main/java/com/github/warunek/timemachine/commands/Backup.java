package com.github.warunek.timemachine.commands;

import com.github.warunek.timemachine.TimeMachine;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Backup {

    final TimeMachine plugin;

    public Backup(final TimeMachine instance) throws Exception {
        plugin = instance;
        backup();
    }

    private void backup() throws Exception {
        List<World> autosave = new ArrayList<>();
        Bukkit.savePlayers();
        for (World loaded : Bukkit.getWorlds()) {
            try {
                loaded.save();
                if (loaded.isAutoSave()) {
                    autosave.add(loaded);
                    loaded.setAutoSave(false);
                }

            } catch (Exception e) {
            }
        }
        new BukkitRunnable(){
            @Override
            public void run() {

                final long time = System.currentTimeMillis();
                Date d = new Date(time);
                File zipFile = new File(plugin.backups, plugin.backupNameFormat.replaceAll("%date%", plugin.dateFormat.format(d)) + ".zip");
                if (!zipFile.exists()) {
                    zipFile.getParentFile().mkdirs();
                    zipFile = new File(plugin.backups, plugin.backupNameFormat.replaceAll("%date%", plugin.dateFormat.format(d)) + ".zip");
                    try {
                        zipFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    zipFolder(plugin.mainDir.getAbsolutePath(), zipFile.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                plugin.isBackingUp = false;
            }
        }.runTaskAsynchronously(plugin);
    }

    private void findSrcFiles(String path, String srcFile, ZipOutputStream zip) {
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
    private void findSrcFolders(String path, String srcFolder, ZipOutputStream zip) {

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

    private void zipFolder(String srcFolder, String destZipFile) throws Exception {
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
