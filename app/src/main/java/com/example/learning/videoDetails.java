package com.example.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class videoDetails extends AppCompatActivity {

    //working fine + its completed if i haven't mistaken
    TextView textViewName,textViewDuration,textViewSize,textViewResolution;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        textViewName=findViewById(R.id.textViewName);
        textViewDuration=findViewById(R.id.textViewDuration);
        textViewSize=findViewById(R.id.textViewSize);
        textViewResolution=findViewById(R.id.textViewResolution);
        textViewName.setText(Constant.name);
        textViewDuration.setText(Constant.duration);
        textViewSize.setText(Constant.size);
        textViewResolution.setText(Constant.resolution);
    }
}