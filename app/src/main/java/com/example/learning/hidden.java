package com.example.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learning.Adapter.RVAdapterHidden;
import com.example.learning.Adapter.RVAdapterLocker;

import java.io.File;


public class hidden extends Fragment {

    private RecyclerView recyclerView;
    private RVAdapterHidden RVAdapterHidden;
    // --Commented out by Inspection (02-Jan-21 8:59 PM):Button ButtonFile,ButtonFolder,ButtonHidden;
//    public SwipeRefreshLayout swipeRefreshLayout;
    public static int themeValue;
    View mView;
    // --Commented out by Inspection (02-Jan-21 8:59 PM):public static boolean isFromHidden=true;
    // --Commented out by Inspection (02-Jan-21 8:59 PM):public String[] allPath;
    Handler mHandler = new Handler(Looper.getMainLooper());
    public static boolean stopProgressBarLoading=true,isFromRefresh=false;
    public static int layoutValueHidden,themeValueHidden;
    public static GridLayoutManager gridLayoutManager;
    public TextView textViewNoFileFoundHidden;
    public LinearLayout linearLayoutLocker;
    public SwitchCompat switchDeepScanning;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hidden,container,false);

        textViewNoFileFoundHidden = mView.findViewById(R.id.textViewNoVideosFoundHidden);
        SharedPreferences prefsForHidden = this.requireActivity().getSharedPreferences("prefsForHidden", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefsForHidden.edit();
        editor.putBoolean("isFromHidden",true);
        editor.apply();


        linearLayoutLocker = mView.findViewById(R.id.linearLayoutLocker);
        linearLayoutLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                try {
                    MainActivity.selectedFragmentValue=7;
                    Constant.allVideoInLocker.clear();
                    RVAdapterLocker.isSelectedLocker.clear();
                    File[] fileList = new File(Constant.appLocation).listFiles();
                    if (fileList != null && fileList.length > 0) {
                        for (File file : fileList) {
                            if (!file.isDirectory()) {
                                Constant.allVideoInLocker.add(file);
                            }
                        }
                    }
                }
                catch (Exception e){
                    Toast.makeText(getActivity(), "something went wrong", Toast.LENGTH_SHORT).show();
                }
                //
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentContainer, new locker())
                        .addToBackStack(null)
                        .commit();
            }
        });

        final SharedPreferences deepScanning = requireActivity().getSharedPreferences("ds",Context.MODE_PRIVATE);
        switchDeepScanning = mView.findViewById(R.id.switchDeepScanning);
        switchDeepScanning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor1 = deepScanning.edit();
                if(isChecked){
                    editor1.putBoolean("ds",true);
                }
                else{
                    editor1.putBoolean( "ds",false);
                }
                editor1.apply();
            }
        });

        switchDeepScanning.setChecked(deepScanning.getBoolean("ds", false));

        return mView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefsLayout2 = this.requireActivity().getSharedPreferences("prefsLayout2",Context.MODE_PRIVATE);
        layoutValueHidden = prefsLayout2.getInt("layoutValue",1);

        recyclerView = mView.findViewById(R.id.recyclerView);
        SharedPreferences prefsThemeValue = this.requireActivity().getSharedPreferences("prefsThemeValue",Context.MODE_PRIVATE);
        themeValueHidden = prefsThemeValue.getInt("selectedTheme",1);
        int orientation = this.getResources().getConfiguration().orientation;
        if(MainActivity.layoutValue==2){
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridLayoutManager = new GridLayoutManager(getActivity(),2,RecyclerView.VERTICAL,false);
            }
            else {
                gridLayoutManager = new GridLayoutManager(getActivity(),4,RecyclerView.VERTICAL,false);
            }
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        else{
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        RVAdapterHidden = new RVAdapterHidden(getActivity());
        recyclerView.setAdapter(RVAdapterHidden);
        for(int i=0;i<Constant.allHiddenMediaList.size();i++){
            RVAdapterHidden.isSelectedHidden.add(false);
        }
        if(Constant.allHiddenMediaList.size()==0){
            textViewNoFileFoundHidden.setVisibility(View.VISIBLE);
        }
        super.onViewCreated(view, savedInstanceState);
    }

}
