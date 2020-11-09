package com.github.zwarunek.timemachine.commands;

import com.github.zwarunek.timemachine.TimeMachine;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ExcludeFileFilter;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Date;

public class Backup {

    static TimeMachine plugin;
    static public int taskIndex;

    public static File backup(final TimeMachine instance, CommandSender sender){
        plugin = instance;
        plugin.isBackingUp = true;
        Bukkit.savePlayers();
        for (World loaded : Bukkit.getWorlds()) {
            try {
                loaded.save();
                if (loaded.isAutoSave()) {
                    loaded.setAutoSave(false);
                }

            } catch (Exception ignored) {}
        }
        Player player;
        BossBar bar = Bukkit.createBossBar(plugin.messages.getProperty("backupBar"), BarColor.GREEN, BarStyle.SOLID);
        if(sender instanceof Player) {
            player = (Player) sender;
            bar.addPlayer(player);
            bar.setVisible(true);
        }
        long time = System.currentTimeMillis();
        Date d = new Date(time);
        ZipFile zipFile = new ZipFile(plugin.backups.getAbsolutePath() + File.separator + plugin.backupNameFormat.replaceAll("%date%", plugin.dateFormat.format(d)) + ".zip");
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        ExcludeFileFilter exclude = file -> {
            for(String folder : plugin.backupFolderExceptions){
                if (file.getName().equalsIgnoreCase(folder)) return true;
            }
            for(String extension : plugin.backupExtensionExceptions){
                if (file.getName().endsWith(extension)) return true;
            }
            return false;
        };
        ZipParameters zipParam = new ZipParameters();
        zipParam.setExcludeFileFilter(exclude);
        zipFile.setRunInThread(true);
        bar.setProgress(0);
        try {
            zipFile.addFolder(new File(plugin.mainDir.getAbsolutePath()), zipParam);
        } catch (ZipException e) {
            plugin.getServer().getConsoleSender().sendMessage(e.getMessage());
        }
        taskIndex = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            boolean print = true;
            @Override
            public void run() {
                if (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                    String progress = plugin.messages.getProperty("backupBarProgress").replaceAll("%PROGRESS%", progressMonitor.getPercentDone() + "");
                    if(sender instanceof  Player) {
                        bar.setProgress(Math.min(((double) progressMonitor.getPercentDone()) / 100, 1.0));
                        bar.setTitle(progress);
                    }
                    if(plugin.backupPrintFrequency > 0 && plugin.backupPrintFrequency < 100) {
                        if (print && progressMonitor.getPercentDone() != 0 && progressMonitor.getPercentDone() % plugin.backupPrintFrequency == 0) {
                            plugin.getServer().getConsoleSender().sendMessage(progress);
                            print = false;
                        } else if (!print && progressMonitor.getPercentDone() % plugin.backupPrintFrequency != 0) {
                            print = true;
                        }
                    }
                }

                else{
                    plugin.isBackingUp = false;
                    bar.removeAll();
                }
            }
        },0, 5);
        return zipFile.getFile();
    }
    public void autosave(TimeMachine plugin){

        new BukkitRunnable() {
            @Override
            public void run() {
                if(plugin.autosaveEnabled) {
                    try {
                        backup(plugin, null);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!plugin.isBackingUp) {
                                    plugin.getServer().getConsoleSender().sendMessage(plugin.messages.getProperty("successPrefix") + plugin.messages.getProperty("backupSuccess"));
                                    Bukkit.getScheduler().cancelTask(Backup.taskIndex);
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(plugin, 20, 5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.runTaskTimer(plugin, plugin.autoBackupFrequency * 20, plugin.autoBackupFrequency * 20);

    }
}
