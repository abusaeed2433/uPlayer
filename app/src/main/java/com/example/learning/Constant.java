package com.example.learning;
import java.io.File;
import java.util.ArrayList;

public class Constant {
    public static final String[] videoExtensions={".mp4",".3gp",".webm",".mkv",".flv",".ogv",".mov","ogg",".avi",".mpg",".mpeg",".m2v"};
    public static final String[] hiddenVideoExtensions={".mp4",".3gp",".webm",".mkv",".flv",".ogv",".mov","ogg",".avi",".mpg",".mpeg",".m2v"};
    public static final ArrayList<File> allMediaList = new ArrayList<>();
    public static final ArrayList<File> allMediaList2 = new ArrayList<>();
    public static final ArrayList<File> allMedia = new ArrayList<>();
    public static final ArrayList<File> allHiddenMediaList = new ArrayList<>();
    public static final ArrayList<File> allFolderList = new ArrayList<>();
    public static final ArrayList<File> allVideoInFolder = new ArrayList<>();
    public static final ArrayList<File> allVideoInLocker = new ArrayList<>();
    public static final ArrayList<Integer> videoCounter = new ArrayList<>();
    public static final ArrayList<Integer> videoInFolderCounter = new ArrayList<>();

    public static String[] allPath;
    public static String defaultLocation;
    //only for search
    public static ArrayList<File> searchItem = new ArrayList<>();
    public static final ArrayList<File> filteredMediaList = new ArrayList<>();
    //only for search above

    public static boolean isLockerPin;

    public static String appLocation;

    public static String height,width,duration,size,name,resolution;

    public static File renameFile;
    public static int renameFilePos,renameFileCheck;
    public static boolean isFromRename;


}
