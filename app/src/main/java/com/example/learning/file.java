package com.example.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learning.Adapter.RVAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;


public class file extends Fragment {
    public static RecyclerView recyclerView;
    private RVAdapter RVAdapter;
    // --Commented out by Inspection (02-Jan-21 8:58 PM):Button ButtonFile,ButtonFolder,ButtonHidden;
    public SwipeRefreshLayout swipeRefreshLayout;
    // --Commented out by Inspection (02-Jan-21 8:59 PM):public static int themeValue,layoutValueFile=1;
    public TextView textViewNoVideosFound;
    // --Commented out by Inspection (02-Jan-21 8:58 PM):public EditText editTextSearch;
    // --Commented out by Inspection (02-Jan-21 8:59 PM):View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View mView;


//        SharedPreferences prefsThemeValue = this.getActivity().getSharedPreferences("prefsThemeValue", Context.MODE_PRIVATE);
//        themeValue = prefsThemeValue.getInt("selectedTheme",1);
//        SharedPreferences prefsLayout = this.getActivity().getSharedPreferences("prefsLayout",Context.MODE_PRIVATE);
//        layoutValueFile = prefsLayout.getInt("layoutValue",1);

        mView = inflater.inflate(R.layout.file,container,false);
        //
        Log.d("allMediaListSizeTest1",String.valueOf(Constant.allMediaList.size()));
        Log.d("allFolderListSizeTest1",String.valueOf(Constant.allFolderList.size()));
        Log.d("allHiddenListSizeTest1",String.valueOf(Constant.allHiddenMediaList.size()));
        //
        textViewNoVideosFound = mView.findViewById(R.id.textViewNoVideosFound);

        swipeRefreshLayout = mView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                //getting all storage path
//                allPath=StorageUtil.getStorageDirectories(getActivity());
                StoragePath storagePath;
                storagePath = new StoragePath(requireActivity().getExternalFilesDirs(null));
                Constant.allPath =  storagePath.getDeviceStorages();
                //getting all storage path above

                Constant.allMediaList.clear();
                Constant.allMediaList2.clear();
                for(String path:Constant.allPath){
                    File directory = new File(path);
                    Method.load_Directory_Files(directory);
                }
                Constant.allMediaList2.addAll(Constant.allMediaList);
                Method.countVideoInFolder();

                //setting selected option default
                RVAdapter.isFromLongPress=false;
                RVAdapter.isSelected.clear();
                RVAdapter.counter=0;
                for(int i=0;i<Constant.allMediaList.size();i++){
                    RVAdapter.isSelected.add(false);
                }
                //setting selected option default above

                Log.d("sortByValueFile",String.valueOf(MainActivity.sortByValue));
                Collections.sort(Constant.allMediaList);
                switch (MainActivity.sortByValue){
                    case 2:
                        Collections.reverse(Constant.allMediaList);
                        break;
                    case 3:
                        long[] allDates = new long[Constant.allMediaList.size()];
                        File[] allFileList= new File[Constant.allMediaList.size()];
                        for(int i=0;i<Constant.allMediaList.size();i++){
                            allDates[i]=Constant.allMediaList.get(i).lastModified();
                            allFileList[i]=Constant.allMediaList.get(i);
                        }
                        for(int i=0;i<Constant.allMediaList.size();i++){
                            for(int j=i+1;j<Constant.allMediaList.size();j++){
                                if(allDates[i]>allDates[j]){
                                    long temp = allDates[i];
                                    allDates[i]=allDates[j];
                                    allDates[j]=temp;

                                    File tempFile = allFileList[i];
                                    allFileList[i]=allFileList[j];
                                    allFileList[j]=tempFile;
                                }
                            }
                        }
                        Constant.allMediaList.clear();
                        Constant.allMediaList.addAll(Arrays.asList(allFileList));
                        break;
                    default:
                        Collections.sort(Constant.allMediaList);
                        break;
                }
                RVAdapter.notifyDataSetChanged();
                if(Constant.allMediaList.size()==0){
                    textViewNoVideosFound.setVisibility(View.VISIBLE);
                }
                else{
                    textViewNoVideosFound.setVisibility(View.INVISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return mView;
    }

    @Override
    public void onViewCreated(View mView, @Nullable Bundle savedInstanceState) {

        recyclerView = mView.findViewById(R.id.recyclerView);
        if(MainActivity.layoutValue==2) {
            GridLayoutManager gridLayoutManager;
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                try {
                    float sizeInPixel = convertDpToPixel((float) 150.0, requireActivity());
                    int gridCount = MainActivity.screenWidth / (int) sizeInPixel;
                    Toast.makeText(getActivity(), String.valueOf(MainActivity.screenHeight), Toast.LENGTH_SHORT).show();
                    gridLayoutManager = new GridLayoutManager(getActivity(), gridCount, RecyclerView.VERTICAL, false);
                }
                catch (Exception e){
                    gridLayoutManager = new GridLayoutManager(getActivity(),2,RecyclerView.VERTICAL,false);
                }
            }
            else {
                try {
                    float sizeInPixel = convertDpToPixel((float) 170.0, requireActivity());
                    int gridCount = MainActivity.screenWidth / (int) sizeInPixel;
                    gridLayoutManager = new GridLayoutManager(getActivity(), gridCount, RecyclerView.VERTICAL, false);
                }
                catch (Exception e){
                    gridLayoutManager = new GridLayoutManager(getActivity(),4,RecyclerView.VERTICAL,false);
                }
            }
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        }
        RVAdapter =new RVAdapter(getActivity());
        recyclerView.setAdapter(RVAdapter);
        for(int i=0;i<Constant.allMediaList.size();i++){
            RVAdapter.isSelected.add(false);
        }

        Log.i("arraySizeFile",String.valueOf(Constant.allMediaList.size()));

        SharedPreferences prefsForHidden = this.requireActivity().getSharedPreferences("prefsForHidden", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsForHidden.edit();
        editor.putBoolean("isFromHidden",false);
        editor.apply();
        if(Constant.allMediaList.size()==0){
            textViewNoVideosFound.setVisibility(View.VISIBLE);
        }
    }
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
