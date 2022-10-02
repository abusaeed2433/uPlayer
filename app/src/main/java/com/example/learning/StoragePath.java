package com.example.learning;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class StoragePath {
    final File[] getExternalFilesDirs;

    public StoragePath(File[] getExternalFilesDirs) {
        this.getExternalFilesDirs = getExternalFilesDirs;
    }


    public String[] getDeviceStorages() {
        List<String> results = new ArrayList<>();

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs;
            System.out.println("external | "+ Arrays.toString(externalDirs));
            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                //commented below lines and testing //problem_may_rise
//                boolean addPath;
//                addPath = Environment.isExternalStorageRemovable(file);
//
//                if (addPath) {
//                    results.add(path);
//                }
                //commented below lines and testing above
                results.add(path);
            }
        //}
        System.out.println("externalResult | "+results);

        if (results.isEmpty()) { //Method 2 for all versions
            final List<String> out = new ArrayList<>();
            String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
            StringBuilder s = new StringBuilder();
            try {
                final Process process = new ProcessBuilder().command("mount")
                        .redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    s.append(new String(buffer));
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            // parse output
            final String[] lines = s.toString().split("\n");
            for (String line : lines) {
                if (!line.toLowerCase(Locale.US).contains("asec")) {
                    if (line.matches(reg)) {
                        String[] parts = line.split(" ");
                        for (String part : parts) {
                            if (part.startsWith("/"))
                                if (!part.toLowerCase(Locale.US).contains("vold"))
                                    out.add(part);
                        }
                    }
                }
            }
            results.addAll(out);
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        //removed below lines because I want to use otg //problem_may_rise
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            for (int i = 0; i < results.size(); i++) {
//                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
//                    Log.d("Tag", results.get(i) + " might not be extSDcard");
//                    results.remove(i--);
//                }
//            }
//        } else {
//            for (int i = 0; i < results.size(); i++) {
//                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
//                    Log.d("Tag", results.get(i) + " might not be extSDcard");
//                    results.remove(i--);
//                }
//            }
//        }
        //removed below lines because I want to use otg above

        //removed below lines and testing //problem_may_rise
        //Get path to the Internal Storage aka ExternalStorageDirectory
//        final String internalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        results.add(0, internalStoragePath);
        //removed below lines and testing above

        System.out.println("externalResult2| "+results);
        String[] storageDirectories = new String[results.size()];

        for (int i = 0; i < results.size(); ++i) {
            storageDirectories[i] = results.get(i);
        }
        return storageDirectories;

    }
}
