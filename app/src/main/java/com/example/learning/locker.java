package com.example.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learning.Adapter.RVAdapterLocker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class locker extends Fragment {

    View mView;
    private RecyclerView recyclerView;
    private FloatingActionButton fabGoBackLocker;
    private RVAdapterLocker RVAdapterLocker;
    public static int layoutValueHidden,themeValueHidden;
    public static GridLayoutManager gridLayoutManager;
    TextView textViewShowMessageTemp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_locker,container,false);

        //initialising all
        textViewShowMessageTemp = mView.findViewById(R.id.showMessageTemp);
        fabGoBackLocker = mView.findViewById(R.id.fabGoBackLocker);
        //initialising all above

        MainActivity.selectedFragmentValue=7;
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(Constant.isLockerPin){
            textViewShowMessageTemp.setVisibility(View.INVISIBLE);
            SharedPreferences prefsLayout2 = this.requireActivity().getSharedPreferences("prefsLayout2", Context.MODE_PRIVATE);
            layoutValueHidden = prefsLayout2.getInt("layoutValue",1);

            recyclerView = mView.findViewById(R.id.recyclerView);
            //loading all hidden files
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
            RVAdapterLocker = new RVAdapterLocker(getActivity());
            recyclerView.setAdapter(RVAdapterLocker);
            for(int i=0;i<Constant.allVideoInLocker.size();i++){
                RVAdapterLocker.isSelectedLocker.add(false);
            }
            if(Constant.allVideoInLocker.size()==0){
                textViewShowMessageTemp.setVisibility(View.VISIBLE);
                textViewShowMessageTemp.setText(getString(R.string.no_videos_found));
            }
        }
        else{
            textViewShowMessageTemp.setText(getString(R.string.under_development));
            textViewShowMessageTemp.setVisibility(View.VISIBLE);
        }
        super.onViewCreated(view,savedInstanceState);


        //listener for fabGoBackLocker
        fabGoBackLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.selectedFragmentValue=4;
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentContainer,new hidden())
                        .commit();
            }
        });
        //listener for fabGoBackLocker above

    }
}
