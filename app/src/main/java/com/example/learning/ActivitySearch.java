package com.example.learning;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learning.Adapter.RVAdapterSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ActivitySearch extends AppCompatActivity {
    public EditText editTextSearch;
    public RecyclerView recyclerViewSearch;
    public TextView textViewNotMatch;
    public RVAdapterSearch RVAdapterSearch;
    public boolean isFromHidden;
    public static String searchText="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        textViewNotMatch  =findViewById(R.id.textViewNotMatch);
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);

        String hintMessage;
        if (MainActivity.selectedFragmentValue == 4) {
            hintMessage = "search in hidden files";
        }
        else if(MainActivity.selectedFragmentValue==7){
            hintMessage = "search in locker";
        }
        else {
            hintMessage = "search in all files";
        }
        editTextSearch.setHint(hintMessage);

        isFromHidden = getIntent().getBooleanExtra("isFromHidden",false);
        //initializing everything above
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        RVAdapterSearch = new RVAdapterSearch(this);
        recyclerViewSearch.setAdapter(RVAdapterSearch);
        Log.d("SearchAllSize", Constant.filteredMediaList.size()+" | "+Constant.allMediaList.size());

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString().toLowerCase();
                searchText=text;
                filter(text);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void filter(String text){
        Log.d("searchQueryText",text);

        Constant.filteredMediaList.clear();
        if(text.length() == 0){
            if(MainActivity.selectedFragmentValue==4) {
                Constant.filteredMediaList.addAll(Constant.allHiddenMediaList);
            }
            else if(MainActivity.selectedFragmentValue==7){
                Constant.filteredMediaList.addAll(Constant.allVideoInLocker);
            }
            else{
                Constant.filteredMediaList.addAll(Constant.allMediaList);
            }
        }
        else{
            ArrayList<File> temp = new ArrayList<>();
            if(MainActivity.selectedFragmentValue==4) {
                temp.addAll(Constant.allHiddenMediaList);
            }
            else if(MainActivity.selectedFragmentValue==7){
                temp.addAll(Constant.allVideoInLocker);
            }
            else{
                temp.addAll(Constant.allMediaList);
            }
            for(File file:temp){
                if(file.getName().toLowerCase().contains(text)){
                    Constant.filteredMediaList.add(file);
                }
            }
        }

        Log.d("SearchAllSize", String.valueOf(Constant.filteredMediaList.size()));
        RVAdapterSearch.notifyDataSetChanged();
        if(Constant.filteredMediaList.size()==0){
            textViewNotMatch.setVisibility(View.VISIBLE);
        }
        else{
            textViewNotMatch.setVisibility(View.INVISIBLE);
        }
    }

}
