package com.github.warunek.timemachine;

import com.github.warunek.timemachine.util.TimeMachineCommand;
import com.github.warunek.timemachine.util.TimeMachineTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TimeMachine extends JavaPlugin{

    public boolean savePluginJars;
    public boolean saveServerJar;
    public int autoBackupFrequency;
    public File backups;
    public File mainDir;
    public File playerDataDir;
    public File pluginDir;
    public static FileFilter datFilefilter;
    public String backupPath;
    public String backupNameFormat;
    public SimpleDateFormat dateFormat;
    public boolean saveConfig = false;
    public boolean isRestoring = false;
    public boolean isBackingUp = false;
    public boolean restorePlayerWithWorld;
    public List<String> backupFilePaths = new ArrayList<>();
    public BukkitRunnable autobackupRunnable;
    public ArrayList<String> exempt;
    public final String version = this.getDescription().getVersion();
    public final List<String> author = this.getDescription().getAuthors();

    public Object getConfigValue(String path, Object defPath){
        if(getConfig().contains(path))
            return getConfig().get(path);
        saveConfig = true;
        getConfig().set(path, defPath);
        return defPath;
    }

//    public static void main(String[] args){
//        String backupDir= ("C:\\Users\\zacha\\Desktop\\1.16.2 server\\backups\\");
//        String fileZip = ("C:\\Users\\zacha\\Desktop\\1.16.2 server\\backups\\Server_Backup_2020-08-24_14-31-38.zip");
//        unzip(fileZip, backupDir);
//
//
//    }
    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();

//                File newFile = new File(destDir + File.separator + fileName);
                File newFile = new File(fileName);
                //create directories for sub directories in zip
                File parent = new File(newFile.getParent());
                if(parent.getName().equalsIgnoreCase("playerdata")) {
                    newFile = new File(destDir + File.separator + parent.getName() + File.separator + newFile.getName());
                    System.out.println("Unzipping to "+newFile.getAbsolutePath());
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onEnable() {
        displayBanner();
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        mainDir = getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
        playerDataDir = new File(mainDir.getAbsolutePath() + File.separator + "world" + File.separator + "playerdata");
        pluginDir = new File(mainDir.getAbsolutePath() + File.separator + "plugins");
        backupPath = (String)getConfigValue("backupFolderDirectory", "");
        backups = new File(mainDir + backupPath + File.separator + "backups" + File.separator);
        exempt = (ArrayList<String>) getConfigValue("backupExceptions", new String[]{"logs"});
        autoBackupFrequency = (int) getConfigValue("autoBackupFrequency", 1440);
        backupNameFormat = (String) getConfigValue("backupNameFormat", "Server Backup %date%");
        dateFormat = new SimpleDateFormat((String)getConfigValue("dateFormat", "yyyy-MM-dd_HH-mm-ss"));
        restorePlayerWithWorld = (boolean) getConfigValue("restorePlayerWithWorld", false);

        final TimeMachineCommand command = new TimeMachineCommand(this);
        final TimeMachineTabCompleter tabCompleter = new TimeMachineTabCompleter(this);
        getCommand("timemachine").setExecutor(command);
        getCommand("timemachine").setTabCompleter(tabCompleter);
        datFilefilter = file -> file.getName().endsWith(".dat");
        if(!backups.exists())
            backups.mkdir();
    }
    public File getMainDir(){
        return mainDir;
    }

    public void restartServer(){

        new BukkitRunnable(){

            @Override
            public void run() {
                Bukkit.spigot().restart();
            }
        }.runTaskLater(this, 100);
    }

    private void displayBanner(){

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " _______             __  ___         __   _         ");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "/_  __(_)_ _  ___   /  |/  /__ _____/ /  (_)__  ___ ");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + " / / / /  ' \\/ -_) / /|_/ / _ `/ __/ _ \\/ / _ \\/ -_)");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "/_/ /_/_/_/_/\\__/ /_/  /_/\\_,_/\\__/_//_/_/_//_/\\__/ ");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY +      "        Version " + ChatColor.GOLD + version);
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY +      "        Author  " + ChatColor.WHITE + author.get(0));

    }

}
