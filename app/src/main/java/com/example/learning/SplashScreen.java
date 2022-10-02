package com.example.learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class SplashScreen extends AppCompatActivity {
    public static File directory;
    public static boolean isFromSplashScreen=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //for locker
        File directory = this.getExternalCacheDir();
        try {
            assert directory != null;
            Constant.appLocation = directory.getAbsolutePath();
            for (int i = Constant.appLocation.length() - 1; i > 0; i--) {
                if (Constant.appLocation.charAt(i) == '/') {
                    Constant.appLocation = Constant.appLocation.substring(0,i+1);
                    break;
                }
            }
            Log.d("mainPath",Constant.appLocation);
        }
        catch (Exception ignored){

        }
        //for locker above

        SharedPreferences sortBy = getSharedPreferences("sortBy",MODE_PRIVATE);
        MainActivity.sortByValue = sortBy.getInt("sortByValue",1);
        Log.d("sortByValueFile",String.valueOf(MainActivity.sortByValue));

        if((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE))== PackageManager.PERMISSION_GRANTED
                && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            Constant.allPath = new StoragePath(getExternalFilesDirs(null)).getDeviceStorages();
            Constant.defaultLocation = Constant.allPath[0];

            SharedPreferences deepScanning = getSharedPreferences("ds",MODE_PRIVATE);

            for(String path:Constant.allPath){
                directory=new File(path);
                Method.load_Directory_Files(directory);
                if(deepScanning.getBoolean("ds",false)) {
                    Method.load_Directory_Files_Hidden(directory);
                }
                Method.load_Directory_Folder(directory);
            }

            Log.d("mediaList21",String.valueOf(Constant.allMediaList));
            Set<File> set = new LinkedHashSet<>(Constant.allMediaList);
            Set<File> set1 = new LinkedHashSet<>(Constant.allHiddenMediaList);
            Set<File> set2 = new LinkedHashSet<>(Constant.allFolderList);

            Constant.allMediaList.clear();
            Constant.allMediaList.addAll(set);
            Constant.allMediaList2.addAll(Constant.allMediaList);
            Log.d("mediaList2",String.valueOf(Constant.allMediaList2));

            Constant.allHiddenMediaList.clear();
            Constant.allHiddenMediaList.addAll(set1);

            Constant.allFolderList.clear();
            Constant.allFolderList.addAll(set2);
            Log.d("folderSize",String.valueOf(Constant.allFolderList.size()));

            Intent intent=new Intent(SplashScreen.this,MainActivity.class);
//            Intent intent = new Intent(SplashScreen.this,MainActivity2.class);
            startActivity(intent);
        }
        else{
            ActivityCompat.requestPermissions(SplashScreen.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constant.allPath = new StoragePath(getExternalFilesDirs(null)).getDeviceStorages();
                Constant.defaultLocation = Constant.allPath[0];

                SharedPreferences deepScanning = getSharedPreferences("ds",MODE_PRIVATE);

                for(String path:Constant.allPath){
                    directory=new File(path);
                    Method.load_Directory_Files(directory);
                    if(deepScanning.getBoolean("ds",false)) {
                        Method.load_Directory_Files_Hidden(directory);
                    }
                    Method.load_Directory_Folder(directory);
                }

                Log.d("mediaList21",String.valueOf(Constant.allMediaList));
                Set<File> set = new LinkedHashSet<>(Constant.allMediaList);
                Set<File> set1 = new LinkedHashSet<>(Constant.allHiddenMediaList);
                Set<File> set2 = new LinkedHashSet<>(Constant.allFolderList);

                Constant.allMediaList.clear();
                Constant.allMediaList.addAll(set);
                Constant.allMediaList2.addAll(Constant.allMediaList);
                Log.d("mediaList2",String.valueOf(Constant.allMediaList2));

                Constant.allHiddenMediaList.clear();
                Constant.allHiddenMediaList.addAll(set1);

                Constant.allFolderList.clear();
                Constant.allFolderList.addAll(set2);
                Log.d("folderSize",String.valueOf(Constant.allFolderList.size()));

                Intent intent=new Intent(SplashScreen.this,MainActivity.class);
//            Intent intent = new Intent(SplashScreen.this,MainActivity2.class);
                startActivity(intent);
//                StoragePath storagePath;
////                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
////                    storagePath = new StoragePath(getExternalFilesDirs(null));
////                }else {
////                    storagePath = new StoragePath();
////                } //have to use for <=23
//                storagePath = new StoragePath(getExternalFilesDirs(null));
//                Constant.allPath = storagePath.getDeviceStorages();
//
//                for(String path:Constant.allPath){
//                    directory=new File(path);
//                    Method.load_Directory_Files(directory);
//                    Method.load_Directory_Files_Hidden(directory);
//                    Method.load_Directory_Folder(directory);
//                }
//                Method.countVideoInFolder();
//                Intent intent=new Intent(SplashScreen.this,MainActivity.class);
//                startActivity(intent);
            }
            else{
                Toast.makeText(this,"Restart and allow permission",Toast.LENGTH_LONG).show();
            }
        }
    }
    public Runnable start = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
            startActivity(intent);
        }
    };
}