package com.github.zwarunek.timemachine;


import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.zwarunek.timemachine.util.RegionFile;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.bukkit.util.Consumer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestClass {
    public static List<String> excludedFolders;
    public static List<String> excludedExtensions;
    public static boolean restorePlayerDataWithWorld = true;
    public static String mainDir = ("C:\\Users\\zacha\\Desktop\\testServer");
    public static String backupDir= ("C:\\Users\\zacha\\Desktop\\testServer\\backups");
    public static String dir= ("C:\\Users\\zacha\\Desktop\\testServer");
    public static String desk = ("C:\\Users\\zacha\\Desktop\\");
    public static String folder = ("testServer/world/");
    public static String del = ("C:\\Users\\zacha\\Desktop\\testServer\\world");
    public static String fileZip = ("C:\\Users\\zacha\\Desktop\\testServer\\backups\\testZip.zip");
    public static String playerUUID = "99e5b621-aecd-402a-8245-5d9f6e7b8a50";
    public static String resourceID = "83621";
    public static String spigotPluginVersion;
    public static void main(String[] args) throws IOException, InterruptedException {
        excludedFolders = Arrays.asList("backups", "cache", "logs");
        excludedExtensions = Arrays.asList("jar", "bat");
        int [][] chunks = new int[][]{
                {0, 0},
                {1, 0},
                {1, -1},
                {1, -2}};
        updateCheck();
        System.out.println("Version: "  + spigotPluginVersion);
    }
    public static void updateCheck() {
        try {
            final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceID).openConnection();
            connection.setRequestMethod("GET");
            spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        } catch (final IOException e) {
//            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ERR_MSG));
            e.printStackTrace();
            return;
        }
    }

}
