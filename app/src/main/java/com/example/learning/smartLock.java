package com.example.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class smartLock extends AppCompatActivity {

    EditText editTextLockPin,editTextConfirmLockPin;
    Button buttonLockSubmit,buttonLockCancel;
    String pin,confirmPin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_lock);

        //back arrow
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //back arrow above

        //initialising editText, button and others
        editTextLockPin= findViewById(R.id.editTextLockPin);
        editTextConfirmLockPin = findViewById(R.id.editTextConfirmLockPin);
        buttonLockSubmit = findViewById(R.id.buttonLockSubmit);
        buttonLockCancel = findViewById(R.id.buttonLockCancel);
        //initialising editText, button and others above

        //initialising buttonCancel
        buttonLockCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smartLock.super.onBackPressed();
            }
        });
        //initialising buttonCancel above

        //initialising buttonSubmit
        buttonLockSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pin = editTextLockPin.getText().toString();
                confirmPin = editTextConfirmLockPin.getText().toString();
                if(pin.equals("")){
                    Toast.makeText(smartLock.this, "enter pin first", Toast.LENGTH_SHORT).show();
                }
                else if(pin.length()!=4){
                    Toast.makeText(smartLock.this, "pin size must be 4", Toast.LENGTH_SHORT).show();
                }
                else if(confirmPin.equals("")){
                    Toast.makeText(smartLock.this, "confirm pin", Toast.LENGTH_SHORT).show();
                }
                else if(confirmPin.length()!=4){
                    Toast.makeText(smartLock.this, "pin size must be 4", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!pin.equals(confirmPin)){
                        Toast.makeText(smartLock.this,"pin doesn't match",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        SharedPreferences allPin = getSharedPreferences("allPin", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = allPin.edit();
                        editor.putString("lockerPin",pin);
                        editor.apply();
                        smartLock.super.onBackPressed();
                    }
                }
            }
        });
        //initialising buttonSubmit above
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


}