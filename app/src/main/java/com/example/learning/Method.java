package com.example.learning;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.net.URLConnection;
import java.util.Objects;


public class Method {

    public static Context mContext;
    public Method(){
        Context mContext = Method.mContext;
    }

    public static void load_Directory_Files(File directory){
        File[] fileList=directory.listFiles();
        if(fileList != null && fileList.length>0){
            for (File file : fileList) {
                boolean goFurther = true;
                for (String root : Constant.allPath) {
                    if (String.valueOf(file).equals(root + "/Android")) {
                        goFurther = false;
                        break;
                    }
                }
                if (String.valueOf(file).contains("/.")) {
                    goFurther = false;
                }
                if (goFurther) {
                    if (file.isDirectory()) {
                        load_Directory_Files(file);
                    } else {
                        String name = file.getName().toLowerCase();
                        for (String extension : Constant.videoExtensions) {
                            if (name.endsWith(extension)) {
                                if (!name.startsWith(".")) {
                                    Constant.allMediaList.add(file);
//                                Constant.searchItem.add(fileList[i]);
//                                Log.d("searchAdding",String.valueOf(fileList[i]));
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    //
    public static void load_Directory_Files_Hidden(File directory){
        File[] fileList=directory.listFiles();
        if(fileList != null && fileList.length>0){
            for (File file : fileList) {
//                Log.d("file",String.valueOf(file));
                if (file.isDirectory() &&
                        !String.valueOf(file).equals(Constant.appLocation.substring(0,Constant.appLocation.length()-1))) {
                    load_Directory_Files_Hidden(file);
                } else {
                    String name = file.getName().toLowerCase();
                    boolean goFurther = true;
                    for (String extension : Constant.hiddenVideoExtensions) {
                        if (name.endsWith(extension)) {
                            goFurther = false;
                            if (name.startsWith(".")) {
                                Constant.allHiddenMediaList.add(file);
                                Log.i("sizeFromMethod", String.valueOf(Constant.allHiddenMediaList.size()));
                            }
                            break;
                        }
                    }
                    long fileSizeInKB = file.length() / 1024;
                    if (fileSizeInKB > 2000 && goFurther) {
                        try {
//                            Uri uri = FileProvider.getUriForFile(Method.mContext,
//                                    BuildConfig.APPLICATION_ID+".provider",file);
//                            Log.d("hiddenFileChecking2",String.valueOf(uri));
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(Method.mContext,Uri.fromFile(file));
//                            retriever.setDataSource(Method.mContext,uri);
//                            retriever.setDataSource(Method.mContext,uri);
                            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                            Log.d("hiddenHasVideo", Objects.requireNonNull(hasVideo));
                            boolean isVideo;
                            isVideo = "yes".equals(hasVideo);
                            if (isVideo) {
                                Constant.allHiddenMediaList.add(file);
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        }
    }


    //load folder that contains video
    public static void load_Directory_Folder(File directory){
        File[] fileList=directory.listFiles();
        if(fileList != null && fileList.length>0){
            for (File file : fileList) {
                boolean goFurther = true;
                for (String root : Constant.allPath) {
                    if (String.valueOf(file).equals(root + "/Android")) {
                        goFurther = false;
                        break;
                    }
                }
                if (String.valueOf(file).contains("/.")) {
                    goFurther = false;
                }
                if (goFurther) {
                    if (file.isDirectory()) {
                        load_Directory_Folder(file);
                    } else {
                        String name = file.getName().toLowerCase();
                        for (String extension : Constant.videoExtensions) {
                            if (name.endsWith(extension)) {
                                int size = Constant.allFolderList.size();
                                if (size > 0 && Constant.allFolderList.get(size - 1).equals(directory)) {
                                } else {
                                    Constant.allFolderList.add(directory);
                                    Log.d("directory",String.valueOf(directory));
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    //load folder that contains video above


    //counting video in those folder that contain video
    public static void countVideoInFolder(){
        for(File directory:Constant.allFolderList){
            File[] fileList;
            fileList = directory.listFiles();

            int videoInSameFolderCounter=0;
            assert fileList != null;
            for (File file : fileList) {
                if (!file.isDirectory()) {
                    String name = file.getName().toLowerCase();
                    for (String extension : Constant.videoExtensions) {
                        if (name.endsWith(extension) && !name.startsWith(".")) {
                            videoInSameFolderCounter++;
                            break;
                        }
                    }
                }
            }
            Constant.videoCounter.add(videoInSameFolderCounter);
        }
        Constant.videoInFolderCounter.add(0);
        int prev=0;
        for(int val:Constant.videoCounter){
            prev+=val;
            Constant.videoInFolderCounter.add(prev);
        }
    }
    //counting video in those folder that contain video above


    // have to test it
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    //have to test it above

    public static void loadVideoFromFolder(int selectedPosition){
        Constant.allVideoInFolder.clear();
        int iniPosition = Constant.videoInFolderCounter.get(selectedPosition);
        int lpPosition = Constant.videoInFolderCounter.get(selectedPosition+1);
        Log.d("videoInFolderCount",String.valueOf(Constant.videoInFolderCounter));
        for(int i=iniPosition;i<lpPosition;i++){
            try {
                Constant.allVideoInFolder.add(Constant.allMediaList2.get(i));
            }
            catch (Exception ignored){

            }
        }
    }

    public static void videoDetails(File file){//todo details button; retrieving all value. have to repair
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        Uri uri = FileProvider.getUriForFile(Method.mContext,
                BuildConfig.APPLICATION_ID + ".provider",file);
//        metaRetriever.setDataSource(Method.mContext,uri);
        try {
            metaRetriever.setDataSource(String.valueOf(file));
        }
        catch (Exception e){
            Constant.name="not found";
            Constant.duration="not found";
            Constant.size="not found";
            Constant.resolution="not found";
        }
        try {
            Constant.name = file.getName();
        }
        catch (Exception e){
            Constant.name="not found";
        }

        try{
            Constant.duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration;
            try {
                assert Constant.duration != null;
                duration = Long.parseLong(Constant.duration);
            }
            catch (Exception e){
                duration = 0;
            }
            duration/=1000; //in sec

            long hour = duration/3600;
            String h = String.valueOf(hour);
            if(h.length()==1){
                h="0"+hour;
            }
            long min = (duration-(hour*3600))/60;
            String m = String.valueOf(min);
            if(m.length()==1){
                m="0"+m;
            }
            long sec = (duration%60);
            String s = String.valueOf(sec);
            if(s.length()==1){
                s="0"+s;
            }
            if(duration<60){
                Constant.duration= sec +"sec";
            }
            else{
                if(h.equals("00")){
                    Constant.duration = m+":"+s;
                }
                else{
                    Constant.duration = h+":"+m+":"+s;
                }
            }
        }
        catch (Exception e){
            Constant.duration="not found";
        }
        try {
            long size = (file.length() / 1024) / 1024;
            if (size < 1024) {
                Constant.size = size +"mb";
            } else {
                Constant.size = (float) size / (float) 1024 +"gb";
            }
        }
        catch (Exception e){
            Constant.size="not found";
        }
        try {
            Constant.height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            Constant.width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            Constant.resolution = Constant.height + "*" + Constant.height;
        }
        catch (Exception e){
            Constant.resolution="not found";
        }
    }

//    public static void convertVideo(String srcPath, final String destPath){
//        VideoSlimmer.convertVideo(srcPath,destPath,
//                200,360,200*360*30,
//                new VideoSlimmer.ProgressListener(){
//            @Override
//            public void onStart() {
//                //convert start
//            }
//            @Override
//            public void onFinish(boolean result) {
//                Log.d("convert","finished");
//                Log.d("destination",destPath);
//                //convert finish,result(true is success,false is fail)
//            }
//
//            @Override
//            public void onProgress(float percent) {
//                //percent of progress
//            }
//        });
//    }
}
