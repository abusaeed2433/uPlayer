package com.example.learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;

public class SetupQuestion extends AppCompatActivity {
    Spinner spinnerQuestion;
    EditText editTextAnswer;
    Button buttonSubmit,buttonCancel,buttonSmartLock;
    public String answer,question;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_question);

        //back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //back button above

        spinnerQuestion = findViewById(R.id.spinnerQuestion);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSmartLock = findViewById(R.id.buttonSmartLock);

        final SharedPreferences prefsAnswer = getSharedPreferences("prefsAnswer",MODE_PRIVATE);
        //button submit listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = editTextAnswer.getText().toString();
                boolean onlySpace=true;
                for(int i=0;i<answer.length();i++){
                    if(!String.valueOf(answer.charAt(i)).equals(" ")){
                        onlySpace=false;
                        break;
                    }
                }
                if(answer.equals("")){
                    Toast.makeText(SetupQuestion.this, "write answer first", Toast.LENGTH_SHORT).show();
                }
                else if(onlySpace){
                    Toast.makeText(SetupQuestion.this, "write some letter", Toast.LENGTH_SHORT).show();
                }
                else if(question.equals("Select your question") || question.equals("")){
                    Toast.makeText(SetupQuestion.this, "Select question first", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SetupQuestion.this, "submitted", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLockScreenFirstTime", false);
                    editor.apply();
                    LockScreen.isLockScreenFirstTime=false;
                    SharedPreferences.Editor editor1 = prefsAnswer.edit();
                    editor1.putString("prefsAnswer",answer);
                    editor1.apply();
                    //
                    SharedPreferences allPin = getSharedPreferences("allPin", Context.MODE_PRIVATE);
                    if(Objects.equals(allPin.getString("lockerPin", ""), "")) {
                        SharedPreferences.Editor editor2 = allPin.edit();
                        editor2.putString("lockerPin", allPin.getString("mainPin",""));
                        editor2.apply();
                    }
                    //
                    new Handler(Looper.getMainLooper()).postDelayed(goBack,500);
                }
            }
        });
        //button submit listener above

        //button cancel listener
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetupQuestion.super.onBackPressed();
            }
        });
        //button cancel listener above

        //initialising spinner
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.securityQuestion,R.layout.spinner_text_view);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerQuestion.setAdapter(adapter);
        spinnerQuestion.setSelection(0);
        spinnerQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                question = adapterView.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = prefsAnswer.edit();
                editor.putString("prefsQuestion",question);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //initialising spinner above

        //button smart lock initialising
        buttonSmartLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetupQuestion.this,smartLock.class));
            }
        });
        //button smart lock initialising
    }

    //for back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //for back arrow above


    public final Runnable goBack = new Runnable() {
        @Override
        public void run() {
            SetupQuestion.super.onBackPressed();
        }
    };


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}