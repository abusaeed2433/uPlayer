package com.example.learning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.example.learning.Adapter.RVAdapter;
import com.example.learning.Adapter.RVAdapterEachFolder;
import com.example.learning.Adapter.RVAdapterHidden;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    public static boolean isFromHidden;


    private ImageView ivSearch, ivUpgrade, ivMore;
    public BottomNavigationView navigationView;
    public static int layoutValue,themeValue,sortByValue,selectedFragmentValue=1,screenHeight,screenWidth;
    public SharedPreferences layoutThemeSortBy;
    public SharedPreferences.Editor layoutThemeSortByEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutThemeSortBy = getSharedPreferences("layoutThemeSortBy",MODE_PRIVATE);
        SplashScreen.isFromSplashScreen=false;

        layoutValue= layoutThemeSortBy.getInt("layoutValue",1);
        themeValue= layoutThemeSortBy.getInt("themeValue",1);
        sortByValue = layoutThemeSortBy.getInt("sortByValue",1);


        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        ivSearch = findViewById(R.id.iv_search);
        ivUpgrade = findViewById(R.id.iv_upgrade);
        ivMore = findViewById(R.id.iv_more);
        navigationView = findViewById(R.id.bnv);
        setClickListener();
        setBnvListener();

        SharedPreferences prefsForHidden = getSharedPreferences("prefsForHidden",MODE_PRIVATE);
        isFromHidden = prefsForHidden.getBoolean("isFromHidden",false);

        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,new file()).commit();


        if(savedInstanceState != null){
            int fragmentPosition = savedInstanceState.getInt("fragmentValue",1);
            Fragment selectedFragment;
            switch (fragmentPosition){
                case 1:
                    selectedFragment = new file();
                    selectedFragmentValue=1;
                    break;
                case 2:
                    selectedFragment = new folder();
                    selectedFragmentValue=2;
                    break;
                case 3:
                    selectedFragment = new LockScreen();
                    selectedFragmentValue=3;
                    break;
                case 4:
                    selectedFragment=new hidden();
                    selectedFragmentValue=4;
                    break;
                case 5:
                    selectedFragment=new videoInFolder();
                    selectedFragmentValue=5;
                    break;
                default:
                    selectedFragment=new file();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, selectedFragment).commit();

        }

    }

    private void setClickListener(){
        ivSearch.setOnClickListener(v -> openSearch());

        ivUpgrade.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,Favourite.class)));

        ivMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this,v);
            popupMenu.getMenuInflater().inflate(R.menu.action_bar,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                checkItem(popupMenu.getMenu(),4);
                menuClickListener(popupMenu.getMenu(),item);
                return true;
            });
            popupMenu.show();
        });

    }

    private void setBnvListener(){
        navigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            int id = item.getItemId();
            if(id == R.id.File) {
                selectedFragmentValue = 1;
                finishMode();
                selectedFragment = new file();
            }
            else if(id == R.id.Folder) {
                selectedFragmentValue = 2;
                finishMode();
                selectedFragment = new folder();
            }
            else if(id == R.id.Hidden) {
                finishMode();
                SharedPreferences prefsForHidden = getSharedPreferences("prefsForHidden", Context.MODE_PRIVATE);
                isFromHidden = prefsForHidden.getBoolean("isFromHidden", false);
                if (isFromHidden) {
                    selectedFragmentValue = 4;
                    selectedFragment = new hidden();
                } else {
                    selectedFragmentValue = 3;
                    selectedFragment = new LockScreen();
                }
            }
            else {
                finishMode();
                selectedFragment = new file();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,selectedFragment).commit();
            return true;
        });
    }

    //action bar item selected listener
    private void menuClickListener(Menu optionMenu,@NonNull MenuItem item) {
        layoutThemeSortByEditor = layoutThemeSortBy.edit();
        int id = item.getItemId();
        if(id == R.id.sortByName1) {
            sortByValue = 1;
            layoutThemeSortByEditor.putInt("sortByValue", sortByValue);
            layoutThemeSortByEditor.apply();
            notifyAdapter();
            checkItem(optionMenu,1);
            item.setCheckable(true);
            item.setChecked(true);
        }
        else if(id == R.id.sortByName2) {
            sortByValue = 2;
            layoutThemeSortByEditor.putInt("sortByValue", sortByValue);
            layoutThemeSortByEditor.apply();
            notifyAdapter();
            checkItem(optionMenu,1);
            item.setCheckable(true);
            item.setChecked(true);
        }
        else if(id == R.id.sortByDate) {
            sortByValue = 3;
            layoutThemeSortByEditor.putInt("sortByValue", sortByValue);
            layoutThemeSortByEditor.apply();
            notifyAdapter();
            checkItem(optionMenu,1);
            item.setCheckable(true);
            item.setChecked(true);
        }
        else if(id == R.id.showAsList) {
            layoutValue = 1;
            layoutThemeSortByEditor.putInt("layoutValue", layoutValue);
            layoutThemeSortByEditor.apply();
            notifyAdapter();
            checkItem(optionMenu,2);
            item.setCheckable(true);
            item.setChecked(true);
        }
        else if(id == R.id.showAsGrid) {
            layoutValue = 2;
            layoutThemeSortByEditor.putInt("layoutValue", layoutValue);
            layoutThemeSortByEditor.apply();
            notifyAdapter();
            checkItem(optionMenu,2);
            item.setCheckable(true);
            item.setChecked(true);
        }
        else if(id == R.id.themeNormal) {
            themeValue = 1;
            layoutThemeSortByEditor.putInt("themeValue", themeValue);
            layoutThemeSortByEditor.apply();
            notifyAdapter();
            checkItem(optionMenu,3);
            item.setCheckable(true);
            item.setChecked(true);
        }
        else if(id == R.id.themColoured) {
            themeValue = 2;
            layoutThemeSortByEditor.putInt("themeValue", themeValue);
            layoutThemeSortByEditor.apply();
            notifyAdapter();
            checkItem(optionMenu,3);
            item.setCheckable(true);
            item.setChecked(true);
        }
        else if(id == R.id.review) {
            Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSearch(){
        Constant.filteredMediaList.clear();
        Intent intent = new Intent(MainActivity.this, ActivitySearch.class);
        if (selectedFragmentValue == 4) {
            Constant.filteredMediaList.addAll(Constant.allHiddenMediaList);
            intent.putExtra("isFromHidden", true);
        } else if (selectedFragmentValue == 7) {
            Constant.filteredMediaList.addAll(Constant.allVideoInLocker);
            intent.putExtra("isFromHidden", true);
        }
        else {
            Constant.filteredMediaList.addAll(Constant.allMediaList);
            intent.putExtra("isFromHidden", false);
        }
        startActivity(intent);
    }


    public void checkItem(Menu optionMenu,int checker){
        switch (checker){
            case 1://sort by
                optionMenu.findItem(R.id.sortByName1).setChecked(false);
                optionMenu.findItem(R.id.sortByName1).setCheckable(false);
                optionMenu.findItem(R.id.sortByName2).setChecked(false);
                optionMenu.findItem(R.id.sortByName2).setCheckable(false);
                optionMenu.findItem(R.id.sortByDate).setChecked(false);
                optionMenu.findItem(R.id.sortByDate).setCheckable(false);
                break;
            case 2://show as
                optionMenu.findItem(R.id.showAsList).setChecked(false);
                optionMenu.findItem(R.id.showAsList).setCheckable(false);
                optionMenu.findItem(R.id.showAsGrid).setChecked(false);
                optionMenu.findItem(R.id.showAsGrid).setCheckable(false);
                break;
            case 3://theme
                optionMenu.findItem(R.id.themColoured).setChecked(false);
                optionMenu.findItem(R.id.themColoured).setCheckable(false);
                optionMenu.findItem(R.id.themeNormal).setChecked(false);
                optionMenu.findItem(R.id.themeNormal).setCheckable(false);
                break;
            default:
                if(layoutValue==1){
                    optionMenu.findItem(R.id.showAsList).setCheckable(true);
                    optionMenu.findItem(R.id.showAsList).setChecked(true);
                }
                else{
                    optionMenu.findItem(R.id.showAsGrid).setCheckable(true);
                    optionMenu.findItem(R.id.showAsGrid).setChecked(true);
                }
                if(themeValue==2) {
                    optionMenu.findItem(R.id.themColoured).setCheckable(true);
                    optionMenu.findItem(R.id.themColoured).setChecked(true);
                }
                else{
                    optionMenu.findItem(R.id.themeNormal).setCheckable(true);
                    optionMenu.findItem(R.id.themeNormal).setChecked(true);
                }
                if(sortByValue==1) {
                    optionMenu.findItem(R.id.sortByName1).setCheckable(true);
                    optionMenu.findItem(R.id.sortByName1).setChecked(true);
                }
                else if(sortByValue==2) {
                    optionMenu.findItem(R.id.sortByName2).setCheckable(true);
                    optionMenu.findItem(R.id.sortByName2).setChecked(true);
                }
                else{
                    optionMenu.findItem(R.id.sortByDate).setCheckable(true);
                    optionMenu.findItem(R.id.sortByDate).setChecked(true);
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        savedInstanceState.putInt("fragmentValue",selectedFragmentValue);
        super.onSaveInstanceState(savedInstanceState);
    }

    //finishing other fragments mode
    public void finishMode(){
        if(RVAdapter.actionMode!=null){
            RVAdapter.actionMode.finish();
        }
        if(RVAdapterEachFolder.actionMode!=null){
            RVAdapterEachFolder.actionMode.finish();
        }
        if(RVAdapterHidden.actionMode!=null){
            RVAdapterHidden.actionMode.finish();
        }
    }
    //finishing other fragments mode above


    public void onBackPressed(){
        RVAdapter.isFromLongPress = false;
        if(selectedFragmentValue!=5 && selectedFragmentValue!=6 && selectedFragmentValue!=7){
            if(selectedFragmentValue==1) {
                Constant.allMediaList.clear();
                Constant.allMediaList2.clear();
                Constant.allFolderList.clear();
                Constant.allHiddenMediaList.clear();
                Constant.videoCounter.clear();

                finish();
            }
            else{
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentContainer,new file())
                        .commit();
                selectedFragmentValue=1;
                navigationView.setSelectedItemId(R.id.File);
            }
        }
        else{
            super.onBackPressed();
            if(selectedFragmentValue==5) {
                selectedFragmentValue = 2;
            }
            else{
                selectedFragmentValue=4;
            }
        }
    }
    
    @Override
    protected void onResume() {

        if(Constant.isFromRename){
            Constant.isFromRename=false;
            notifyAdapter();
        }
        else{
            if(selectedFragmentValue==3){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentContainer,new LockScreen())
                        .commit();
            }
        }
        super.onResume();
    }

    public void notifyAdapter() {
        Fragment fragment;
        switch (selectedFragmentValue){
            case 2:
                fragment = new folder();
                break;
            case 3:
                SharedPreferences prefsForHidden = getSharedPreferences("prefsForHidden", Context.MODE_PRIVATE);
                isFromHidden = prefsForHidden.getBoolean("isFromHidden",false);
                if(isFromHidden){
                    fragment = new hidden();
                }
                else {
                    fragment = new LockScreen();
                }
                break;
            case 4:
                fragment = new hidden();
                break;
            case 5:
                fragment = new videoInFolder();
                break;
            case 7:
                fragment = new locker();
                break;
            default:
                fragment = new file();
                break;
        }
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if(selectedFragmentValue!=3 || !RVAdapterHidden.isFromPlayerHidden
                || prefs.getBoolean("isLockScreenFirstTime",true)){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.replace(R.id.FragmentContainer, fragment).commit();
        }
    }

}