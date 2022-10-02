package com.example.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learning.Adapter.RVAdapterEachFolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class videoInFolder extends Fragment {
    View mView;
    public RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public FloatingActionButton fabGoBack;
    public int themeValue,layoutValueFile;
    public TextView textViewShowMessage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video_in_folder,container,false);

        //for layout and theme
        SharedPreferences prefsThemeValue = this.requireActivity().getSharedPreferences("prefsThemeValue", Context.MODE_PRIVATE);
        themeValue = prefsThemeValue.getInt("selectedTheme",1);
        SharedPreferences prefsLayout = this.requireActivity().getSharedPreferences("prefsLayout",Context.MODE_PRIVATE);
        layoutValueFile = prefsLayout.getInt("layoutValue",1);
        //for layout and theme above

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RVAdapterEachFolder.isSelectedFolder.clear();
        for(int i=0;i<Constant.allVideoInFolder.size();i++){
            RVAdapterEachFolder.isSelectedFolder.add(false);
        }

        //initialising all
        recyclerView = mView.findViewById(R.id.recyclerView);
        fabGoBack = mView.findViewById(R.id.fabGoBack);
        textViewShowMessage = mView.findViewById(R.id.textViewShowMessage);
        //initialising all above

        if(Constant.allVideoInFolder.size()==0){
            textViewShowMessage.setVisibility(View.VISIBLE);
        }
        else{
            textViewShowMessage.setVisibility(View.INVISIBLE);
        }

        if(MainActivity.layoutValue==2) {
            GridLayoutManager gridLayoutManager;
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                float sizeInPixel = convertDpToPixel((float) 150.0, requireActivity());
                int gridCount = MainActivity.screenWidth/(int)sizeInPixel;
                Toast.makeText(getActivity(), String.valueOf(MainActivity.screenHeight), Toast.LENGTH_SHORT).show();
                gridLayoutManager = new GridLayoutManager(getActivity(),gridCount,RecyclerView.VERTICAL,false);
            }
            else {
                float sizeInPixel = convertDpToPixel((float) 170.0, requireActivity());
                int gridCount = MainActivity.screenWidth/(int)sizeInPixel;
                gridLayoutManager = new GridLayoutManager(getActivity(),gridCount,RecyclerView.VERTICAL,false);
            }
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        }
        //
        RVAdapterEachFolder RVAdapterEachFolder = new RVAdapterEachFolder(getActivity());
        recyclerView.setAdapter(RVAdapterEachFolder);
        //loading all files from splash screen above
        super.onViewCreated(view, savedInstanceState);

        //listener for fabGoBack
        fabGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.selectedFragmentValue=2;
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentContainer,new folder())
                        .commit();
            }
        });
        //listener for fabGoBack above

    }
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
