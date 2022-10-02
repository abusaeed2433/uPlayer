package com.example.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learning.Adapter.RVAdapterFolder;

import java.io.File;


public class folder extends Fragment {
    View mView;
    RecyclerView recyclerViewFolder;
    public SwipeRefreshLayout swipeRefreshLayoutFolder;
    public String[] allPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.folder,container,false);
        swipeRefreshLayoutFolder = mView.findViewById(R.id.swipeRefreshLayoutFolder);

        //deleting file in all files will take effect here
        Constant.videoCounter.clear();
        Constant.videoInFolderCounter.clear();
        Method.countVideoInFolder();
        //deleting file in all files will take effect here above

        //
        Log.d("allMediaListSizeTest2",String.valueOf(Constant.allMediaList.size()));
        Log.d("allFolderListSizeTest2",String.valueOf(Constant.allFolderList.size()));
        Log.d("allHiddenListSizeTest2",String.valueOf(Constant.allHiddenMediaList.size()));
        //
//        swipeRefreshLayoutFolder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        swipeRefreshLayoutFolder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                StoragePath storagePath = new StoragePath(requireActivity().getExternalFilesDirs(null));
                allPath =  storagePath.getDeviceStorages();
                //getting all storage path above

                Constant.allFolderList.clear();
                Constant.videoCounter.clear();
                Constant.videoInFolderCounter.clear();
                Constant.allMediaList.clear();
                for(String path:allPath){
                    File directory = new File(path);
                    Method.load_Directory_Files(directory);
                    Method.load_Directory_Folder(directory);
                }
                Constant.allMediaList2.clear();
                Constant.allMediaList2.addAll(Constant.allMediaList);
                Log.d("allFolder",String.valueOf(Constant.allFolderList));
                Method.countVideoInFolder();
                //
                int orientation = requireActivity().getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    recyclerViewFolder.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
                }
                else {
                    recyclerViewFolder.setLayoutManager(new GridLayoutManager(getActivity(),2,RecyclerView.VERTICAL,false));
                }
                //
                RVAdapterFolder RVAdapterFolder = new RVAdapterFolder(getActivity());
                recyclerViewFolder.setAdapter(RVAdapterFolder);
//                recyclerViewAdapterFolder.notifyDataSetChanged();
                //its like "nai mamar cheye kana mama valo". above commenting line crashing app
                swipeRefreshLayoutFolder.setRefreshing(false);
            }
        });
        //
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefsForHidden = this.requireActivity().getSharedPreferences("prefsForHidden", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsForHidden.edit();
        editor.putBoolean("isFromHidden",false);
        editor.apply();

        recyclerViewFolder = mView.findViewById(R.id.recyclerViewFolder);
        //setting up for portrait and landscape mode
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewFolder.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        }
        else {
            recyclerViewFolder.setLayoutManager(new GridLayoutManager(getActivity(),2,RecyclerView.VERTICAL,false));
        }
        //setting up for portrait and landscape mode above

        //loading all files from splash screen
        RVAdapterFolder RVAdapterFolder = new RVAdapterFolder(getActivity());
        recyclerViewFolder.setAdapter(RVAdapterFolder);
        //loading all files from splash screen above

        super.onViewCreated(view, savedInstanceState);
    }

}
