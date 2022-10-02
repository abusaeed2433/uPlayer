package com.example.learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;


public class LockScreen extends Fragment implements View.OnClickListener{
    public TextView TextViewFirstTime,TextViewTemporary,TextViewShowPin;
    final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Button[] buttons = new Button[10];
    private ImageButton ButtonBackSpace;
    private ImageView ivNotifyExtra;
    private Button buttonForgotPin;
    public static String showPin = "", prevPin="";
    public boolean isInCreatePin = true, isPinFirstTime = false,isPinSecondTime = false,isLockFirstTime;
    public static boolean isLockScreenFirstTime;
    String mainPin,lockerPin;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_lock_screen,container,false);
        TextViewFirstTime = mView.findViewById(R.id.TextViewFirstTime);
        TextViewShowPin = mView.findViewById(R.id.TextViewShowPin);
        TextViewTemporary = mView.findViewById(R.id.TextViewTemporary);

        MainActivity.selectedFragmentValue=3;

        //information about extra file
        ivNotifyExtra = mView.findViewById(R.id.buttonNotifyExtra);
        ivNotifyExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogAboutExtra();
            }
        });
        //information about extra file above

        //forgot pin button
        buttonForgotPin = mView.findViewById(R.id.buttonForgotPin);
        //forgot pin button above


        TextViewShowPin.setText(showPin);

        //
        SharedPreferences prefs = this.requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        isLockScreenFirstTime = prefs.getBoolean("isLockScreenFirstTime",true);

        SharedPreferences allPin = requireActivity().getSharedPreferences("allPin",Context.MODE_PRIVATE);
        mainPin = allPin.getString("mainPin","0");
        lockerPin = allPin.getString("lockerPin","0");

        // checking if pin created or not
        if(isLockScreenFirstTime){
            TextViewFirstTime.setText(getString(R.string.create_a_pin));
        }
        else{
            TextViewFirstTime.setText(getString(R.string.enter_pin));
        }
        //checking if pin created or not above

        //initialising button and setting onclicklistener
        for(int i=0;i<10;i++){
            String ButtonId="button"+i;
            int resId=getResources().getIdentifier(ButtonId,"id", requireActivity().getPackageName());
            buttons[i]=mView.findViewById(resId);
            buttons[i].setOnClickListener(this);
        }
        //initialising button and setting onclicklistener above

        //button backspace
        ButtonBackSpace = mView.findViewById(R.id.button10);
        ButtonBackSpace.setOnClickListener(view -> {
            if(showPin.length()>0){
                showPin = showPin.substring(0,showPin.length()-1);
                TextViewShowPin.setText(showPin);
            }
        });
        //button backspace above

        //forgot pin button listener
        buttonForgotPin.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ForgotPin.class);
            startActivity(intent);
        });
        //forgot pin button listener above


        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void onClick(final View v){
        String temp = ((Button)v).getText().toString();
        showPin+=temp;
        if(showPin.length()<=4){
            TextViewShowPin.setText(showPin);
            if(showPin.length()==4) {
                if(isLockScreenFirstTime){
                    if (isPinFirstTime) { // !isPinFirstTime (i have done it oppositely by mistake)
                        if (showPin.equals(prevPin)) {
                            isPinSecondTime = true;
                            TextViewTemporary.setText("");

                            SharedPreferences allPin = requireActivity().getSharedPreferences("allPin",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = allPin.edit();
                            editor.putString("mainPin",showPin);
                            editor.apply();

                            //opening for security question
                            Intent intent = new Intent(getActivity(), SetupQuestion.class);
                            startActivity(intent);
                            //opening for security question above
                        }
                        else {
                            isPinFirstTime = false;
                            isPinSecondTime = false;
                            TextViewTemporary.setText(getString(R.string.pin_does_not_match));
                        }
                    }
                    else {
                        prevPin = showPin;
                        isPinFirstTime = true;
                        TextViewTemporary.setText(getString(R.string.enter_again));
                    }
                }
                else{
                    if(showPin.equals(mainPin) || showPin.equals(lockerPin)){
                        TextViewTemporary.setText(getString(R.string.successful));
                        MainActivity.selectedFragmentValue=4;

                        Constant.isLockerPin= showPin.equals(lockerPin);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.FragmentContainer,new hidden())
                                .commit();
                    }
                    else{
                        TextViewTemporary.setText(getString(R.string.incorrect_pin));
                        mHandler.removeCallbacks(resetTemporaryText);
                        mHandler.postDelayed(resetTemporaryText,500);
                    }
                }
                showPin = "";
                mHandler.removeCallbacks(removePin);
                mHandler.postDelayed(removePin, 1000);
            }
        }
        else{
            TextViewShowPin.setText("");
            showPin="";
        }
    }

    //show dialog about extra
    public void ShowDialogAboutExtra(){
        final Dialog dialog = new Dialog(requireActivity(),R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notify_about_extra);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.BOTTOM);
        dialog.show();
        Button dialogButton = dialog.findViewById(R.id.dialogButton);
        dialogButton.setOnClickListener(view -> dialog.cancel());
    }
    //show dialog about extra above

    //restore everything above

    public final Runnable removePin = new Runnable() {
        @Override
        public void run() {
            TextViewShowPin.setText("");
        }
    };

    public final Runnable resetTemporaryText = new Runnable() {
        @Override
        public void run() {
            TextViewTemporary.setText("");
        }
    };
}