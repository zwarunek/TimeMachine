package com.github.zwarunek.timemachine;


import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.zwarunek.timemachine.util.RegionFile;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

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
    public static void main(String[] args) throws IOException, InterruptedException {
        excludedFolders = Arrays.asList("backups", "cache", "logs");
        excludedExtensions = Arrays.asList("jar", "bat");
        int [][] chunks = new int[][]{
                {0, 0},
                {1, 0},
                {1, -1},
                {1, -2}};
//        System.out.println("r." + (chunks[0][0] >> 5) + "." + (chunks[0][1] >> 5) + ".mca");
//        int x = chunks[0][0] % (32);
//        int y = chunks[0][1] % (32);
//        System.out.println((x<0?x+32:x) + ", " + (y<0?y+32:y));
//        net.minecraft.world.level.chunk.storage.RegionFile test = new RegionFile(new File("C:\\Users\\zacha\\Desktop\\testServer\\world\\region\\r.0.0.mca"));
//        net.minecraft.world.level.chunk.storage.RegionFile backed = new RegionFile(new File("C:\\Users\\zacha\\Desktop\\testServer\\backups\\r.0.0.mca"));
//        com.mojang.nbt.CompoundTag tag = NbtIo.read(backed.getChunkDataInputStream((x<0?x+32:x),(y<0?y+32:y)));
//        NbtIo.write(tag, test.getChunkDataOutputStream((x<0?x+32:x),(y<0?y+32:y)));
//        System.out.println(((CompoundTag)NBTIO.readTag((InputStream) test.getChunkDataInputStream((x<0?x+32:x),(y<0?y+32:y)))).get("DataVersion"));
//        System.out.println(new MinecraftRegion(new File("C:\\Users\\zacha\\Desktop\\testServer\\world\\region\\r.0.0.mca")).getChunk(-1, -1).getBlockID(0, 0, 0));
//        System.out.println(Arrays.toString(chunks[0]));
//        String playerUUID = "all";\
//        ZipFile zip = new ZipFile(fileZip);
//        System.out.println(zip.getFileHeaders().toString());
//        zip.extractFile(folder + "playerdata" +"/", backupDir, "TempPlayerFiles");
//        CompoundTag newTag = NBTIO.readFile(backupDir + File.separator + "testFile.dat");
//        CompoundTag oldTag = NBTIO.readFile(del + File.separator + "playerdata" + File.separator + playerUUID +".dat");
//        oldTag.put(new ListTag("Inventory"));
//        NBTIO.writeFile(oldTag, del + File.separator + "playerdata" + File.separator + playerUUID +".dat");

//        File playerdata = new File(del + File.separator + "playerdata");
//        File[] players = playerdata.listFiles(pathname -> FilenameUtils.isExtension(pathname.getAbsolutePath(), "dat"));

//        System.out.println(Arrays.toString(new File(delFolder).listFiles((dir1, name) -> !name.equalsIgnoreCase("playerData") && !name.equalsIgnoreCase("advancements"))));
//            unzip(fileZip, backupDir, Arrays.asList("advancements", "playerdata"), "world");
//        zip(backupDir, dir);
        restoreChunk(fileZip, "world", chunks);
//        restorePlayer(fileZip, playerUUID, "enderchest");
//        restoreWorld(fileZip, "world");
//        restoreServer(fileZip);

//        unzip(fileZip, desk, folder, del, );
        System.exit(0);
    }
    public static void restoreChunk(String backup,String world, int[][] chunks) throws IOException, InterruptedException {
        for(int[] chunk : chunks) {
            String regionFileName = "r." + (chunk[0] >> 5) + "." + (chunk[1] >> 5) + ".mca";
            File currentFile = null;
            int x = chunks[0][0] % (32);
            int y = chunks[0][1] % (32);
            CompoundTag currentTag;
            RegionFile regionFile;
            currentFile = new File(del + File.separator + "region" + File.separator + regionFileName);
            if(!currentFile.exists()){
                System.out.println("Handle this exception");
                return;
            }
            regionFile = new RegionFile(currentFile);

            ZipFile zip = new ZipFile(backup);
            zip.setRunInThread(false);
            zip.extractFile(new File(mainDir).getName() + "/world/region/" + regionFileName, backupDir, regionFileName);
            File backedFile = new File(backupDir + File.separator + regionFileName);
            DataOutputStream stream = regionFile.getChunkDataOutputStream((x<0?x+32:x),(y<0?y+32:y));
            RegionFile region = new RegionFile(backedFile);
            DataInputStream in = region.getChunkDataInputStream((x<0?x+32:x),(y<0?y+32:y));

            Tag tag =  NBTIO.readTag((InputStream) in);
            NBTIO.writeTag((OutputStream) stream, tag);
            stream.flush();
            stream.close();

            in.close();
            FileUtils.forceDelete(new File(backupDir + File.separator + regionFileName));
        }
    }
//    public static void restorePlayer(String backup, String player, String part) throws IOException, InterruptedException {
//        switch(part.toLowerCase()){
//            case "inventory":
//                part = "Inventory";
//                break;
//            case "enderchest":
//                part = "EnderItems";
//                break;
//        }
//        if(player.equalsIgnoreCase("all")){
//            if(part.equalsIgnoreCase("all")) {
//                String dest = new File(mainDir).getParent();
//                String folder = (new File(mainDir).getName() + "/world/playerdata/");
//                String del = mainDir + File.separator + "world" + File.separator + "playerdata";
//                List<String> filter = new ArrayList<String>();
//                unzip(backup, dest, folder, del, filter);
//                folder = (new File(mainDir).getName() + "/world/advancements/");
//                del = mainDir + File.separator + "world" + File.separator + "advancements";
//                unzip(backup, dest, folder, del, filter);
//            }
//            else{
//                ZipFile zip = new ZipFile(backup);
//                zip.extractFile(new File(mainDir).getName() + "/world/playerdata/", backupDir, "TempPlayerFiles");
//
//                FilenameFilter fileNameFilter = (dir, name) -> name.endsWith(".dat");
//
//                File currentPlayerdata = new File(del + File.separator + "playerdata");
//                String[] currentPlayers = Objects.requireNonNull(currentPlayerdata.list(fileNameFilter));
//
//                File backedPlayerdata = new File(backupDir + File.separator + "TempPlayerFiles");
//                List<String> backedPlayers = Arrays.asList(Objects.requireNonNull(backedPlayerdata.list(fileNameFilter)));
//
//                for(String playerFile : currentPlayers){
//                    String uuid = new File(playerFile).getName();
//                    CompoundTag currentTag = NBTIO.readFile(del + File.separator + "playerdata" + File.separator + uuid);
//                    currentTag.put(new ListTag(part));
//
//                    if(backedPlayers.contains(uuid)) {
//
//                        CompoundTag backedTag = NBTIO.readFile(backupDir + File.separator + "TempPlayerFiles" + File.separator + uuid);
//                        currentTag.put(backedTag.get(part));
//                        NBTIO.writeFile(currentTag, del + File.separator + "playerdata" + File.separator + uuid);
//                    }
//                }
//                FileUtils.forceDelete(new File(backupDir + File.separator + "TempPlayerFiles"));
//            }
//        }
//        else{
//            if(part.equalsIgnoreCase("all")) {
//                String dest = new File(mainDir).getParent();
//                String folder = (new File(mainDir).getName() + "/world/playerdata/" + player + ".dat");
//                List<String> filter = new ArrayList<String>();
//                unzip(backup, dest, folder, null, filter);
//                folder = (new File(mainDir).getName() + "/world/playerdata/" + player + ".dat_old");
//                unzip(backup, dest, folder, null, filter);
//                folder = (new File(mainDir).getName() + "/world/advancements/" + player + ".json");
//                unzip(backup, dest, folder, null, filter);
//            }
//            else{
//                ZipFile zip = new ZipFile(backup);
//                zip.extractFile(new File(mainDir).getName() + "/world/playerdata/" + player + ".dat", backupDir, "TempPlayerFile.dat");
//                CompoundTag backedTag = NBTIO.readFile(backupDir + File.separator + "TempPlayerFile.dat");
//                CompoundTag currentTag = NBTIO.readFile(del + File.separator + "playerdata" + File.separator + playerUUID +".dat");
//                currentTag.put(backedTag.get(part));
//                NBTIO.writeFile(currentTag, del + File.separator + "playerdata" + File.separator + playerUUID +".dat");
//                FileUtils.forceDelete(new File(backupDir + File.separator + "TempPlayerFile.dat"));
//            }
//
//        }
//    }
//    public static void restoreWorld(String backup, String world) throws IOException, InterruptedException {
//        String[] worlds;
//        if(world.equalsIgnoreCase("all"))
//            worlds = new String[]{"world", "world_nether", "world_the_end"};
//        else
//            worlds = new String[]{world};
//
//        for(String worldName : worlds) {
//            String dest = new File(mainDir).getParent();
//            String folder = (new File(mainDir).getName() + "/" + worldName + "/");
//            String del = mainDir + File.separator + worldName;
//            List<String> filter = restorePlayerDataWithWorld ? new ArrayList<String>() : Arrays.asList("playerdata", "advancements");
//            unzip(backup, dest, folder, del, filter);
//        }
//    }
//    public static void restoreServer(String backup) throws IOException, InterruptedException {
//
//        String dest = new File(mainDir).getParent();
//        List<String> filter = new ArrayList<>();
//        unzip(backup, dest, null, null, filter);
//    }
//
//    public static void unzip(String file, String dest, @Nullable String folder, @Nullable String del, List<String> filter) throws IOException, InterruptedException {
//        File tempZip = new File(new File(file).getParent() + File.separator + "TEMP.zip");
//        FileUtils.copyFile(new File(file), tempZip);
//        FilenameFilter test = new FilenameFilter(){
//
//            @Override
//            public boolean accept(File dir, String name) {
//                return !filter.contains(name);
//            }
//        };
//        if(del!=null) {
//            for (File file1 : new File(del).listFiles(test)) {
//                FileUtils.forceDelete(file1);
//            }
//        }
//        ZipFile zipFile = new ZipFile(tempZip);
//
//        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
//        for(String excludedFolder : filter) {
//            System.out.println(folder + excludedFolder + "/");
//            zipFile.removeFile(folder + excludedFolder + "/");
//        }
//
//        zipFile.setRunInThread(true);
//        if(folder == null)
//            zipFile.extractAll(dest);
//        else
//            zipFile.extractFile(folder, dest);
//        while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
//
//            Thread.sleep(100);
//        }
//        FileUtils.forceDelete(tempZip);
//    }
//    public static void backup(String dest, String dir) throws ZipException, InterruptedException {
//        ZipFile zipFile = new ZipFile(dest + File.separator + "testZip.zip");
////        ZipFile zipFile = new ZipFile(dest/*plugin.backups + plugin.backupNameFormat.replaceAll("%date%", plugin.dateFormat.format(d)) + ".zip"*/);
//
//        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
//
//        ExcludeFileFilter exclude = file -> excludedFolders.contains(file.getName()) || excludedExtensions.contains(FilenameUtils.getExtension(file.getName()));
//        ZipParameters zipParam = new ZipParameters();
//        zipParam.setExcludeFileFilter(exclude);
//        zipFile.setRunInThread(true);
//        zipFile.addFolder(new File(dir), zipParam);
//        while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
//            System.out.println("Percentage done: " + progressMonitor.getPercentDone());
//
//            Thread.sleep(100);
//        }
//
//        if (progressMonitor.getResult().equals(ProgressMonitor.Result.SUCCESS)) {
//            System.out.println("Successfully added folder to zip");
//        } else if (progressMonitor.getResult().equals(ProgressMonitor.Result.ERROR)) {
//            System.out.println("Error occurred. Error message: " + progressMonitor.getException().getMessage());
//        } else if (progressMonitor.getResult().equals(ProgressMonitor.Result.CANCELLED)) {
//            System.out.println("Task cancelled");
//        }
//    }
//    private static void unzip(String zipFilePath, String destDir, List<String> exlusions, String parentName) {
//        File dir = new File(destDir);
//        // create output directory if it doesn't exist
//        if(!dir.exists()) dir.mkdirs();
//        FileInputStream fis;
//        //buffer for read and write data to file
//        byte[] buffer = new byte[1024];
//        try {
//            fis = new FileInputStream(zipFilePath);
//            ZipInputStream zis = new ZipInputStream(fis);
//            ZipEntry ze = zis.getNextEntry();
//            while(ze != null){
//                String fileName = ze.getName();
//
////                File newFile = new File(destDir + File.separator + fileName);
//                File newFile = new File(fileName);
//                //create directories for sub directories in zip
//                File parent = new File(newFile.getParent());
//                if(parent.getName().equalsIgnoreCase(parentName)) {
//                    newFile = new File(destDir + File.separator + parent.getName() + File.separator + newFile.getName());
//                    System.out.println("Unzipping to "+newFile.getAbsolutePath());
//                    new File(newFile.getParent()).mkdirs();
//                    FileOutputStream fos = new FileOutputStream(newFile);
//                    int len;
//                    while ((len = zis.read(buffer)) > 0) {
//                        fos.write(buffer, 0, len);
//                    }
//                    fos.close();
//                }
//                //close this ZipEntry
//                zis.closeEntry();
//                ze = zis.getNextEntry();
//            }
//            //close last ZipEntry
//            zis.closeEntry();
//            zis.close();
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

}
