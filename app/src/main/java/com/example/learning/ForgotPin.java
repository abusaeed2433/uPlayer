package com.example.learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ForgotPin extends AppCompatActivity {

    public TextView textViewQuestion;
    public String question,answer;
    public EditText editTextAnswer;
    public Button buttonSubmit,buttonCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);


        //back arrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //back arrow above

        //initialising all views
        textViewQuestion = findViewById(R.id.textViewQuestion);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonCancel = findViewById(R.id.buttonCancel);
        //initialising all views above

        SharedPreferences prefsAnswer = getSharedPreferences("prefsAnswer",MODE_PRIVATE);
        question = prefsAnswer.getString("prefsQuestion"," ");
        answer = prefsAnswer.getString("prefsAnswer"," ");


        if(question.equals("Select your question")){
            textViewQuestion.setText(getString(R.string.setup_pin_first));
        }
        else{
            textViewQuestion.setText(question);
        }

        //button submit initialising
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String givenAnswer = editTextAnswer.getText().toString();
                if(givenAnswer.equals("")){
                    Toast.makeText(ForgotPin.this, "write answer first", Toast.LENGTH_SHORT).show();
                }
                else if(givenAnswer.equals(answer)){
                    Toast.makeText(ForgotPin.this, "create a new pin", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLockScreenFirstTime",true);
                    LockScreen.isLockScreenFirstTime=true;
                    editor.apply();

                    //
                    SharedPreferences allPin = getSharedPreferences("allPin",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = allPin.edit();
                    editor1.putString("mainPin","");
                    editor1.putString("lockerPin","");
                    editor1.apply();
                    //
                    new Handler(Looper.getMainLooper()).postDelayed(goBack,500);
                }
                else{
                    Toast.makeText(ForgotPin.this, "wrong answer", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //button submit initialising

        //button cancel initialising
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPin.super.onBackPressed();
                Toast.makeText(ForgotPin.this, "cancel", Toast.LENGTH_SHORT).show();
            }
        });
        //button cancel initialising above

        //retrieving data in orientation changes
        if(savedInstanceState != null){
            editTextAnswer.setText(savedInstanceState.getString("takingAnswer",""));
        }
        //retrieving data in orientation changes above


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


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("takingAnswer",editTextAnswer.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public final Runnable goBack = new Runnable() {
        @Override
        public void run() {
            ForgotPin.super.onBackPressed();
        }
    };
    
}