package com.example.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class videoRename extends AppCompatActivity {

    EditText editTextRename;
    Button buttonCancelRename,buttonOkRename;
    int lastPos=-1,pos=-1;
    final String fileName = Constant.renameFile.getName();
	String extension;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_rename);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        editTextRename = findViewById(R.id.editTextRename);
        buttonCancelRename = findViewById(R.id.buttonCancelRename);
        buttonOkRename = findViewById(R.id.buttonOkRename);

        //button cancel listener
        buttonCancelRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //button cancel listener above

        final String path = String.valueOf(Constant.renameFile).substring(0, String.valueOf(Constant.renameFile).length()-Constant.renameFile.getName().length());
        Log.d("path",path);

        while (true){
            pos = fileName.indexOf('.',pos+1);
            if(pos==-1 || lastPos==pos){
                break;
            }
            else{
                lastPos=pos;
            }
        }
        extension = fileName.substring(lastPos);
        editTextRename.setText(fileName.substring(0,lastPos));
        editTextRename.selectAll();
        editTextRename.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //for closing not needed now

        final String forbiddenChar = "?:\"*|/\\<>";
        final boolean[] checker = {true};
        final int[] counter = {0};
        editTextRename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = String.valueOf(editable);
                if(check(forbiddenChar,text)){
                    editTextRename.setBackgroundResource(R.drawable.background_edit_text_2);
                    checker[0]=false;
                }
                else{
                    if(!checker[0]){
                        editTextRename.setBackgroundResource(R.drawable.background_edit_text);
                    }
                }
            }
        });
        //button ok listener
        buttonOkRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextRename.getText().toString();
                if(name.trim().equals("")){
                    Toast.makeText(videoRename.this, "Enter name first", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(name.length()>127){
                        Toast.makeText(videoRename.this, "Size too long", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try {
                            Log.d("pathRename",path+name+extension);
                            org.apache.commons.io.FileUtils.moveFile(Constant.renameFile,new File(path+name+extension));
                            Log.d("renameFileCheck",String.valueOf(Constant.renameFileCheck));
                            switch (Constant.renameFileCheck){
                                case 2:
                                    Constant.allVideoInFolder.set(Constant.renameFilePos,new File(path+name+extension));
                                    Constant.allMediaList.set(Constant.allMediaList.indexOf(Constant.renameFile),
                                            Constant.allVideoInFolder.get(Constant.renameFilePos));
                                    Constant.allMediaList2.set(Constant.allMediaList2.indexOf(Constant.renameFile),
                                            Constant.allVideoInFolder.get(Constant.renameFilePos));
                                    Constant.isFromRename=true;
                                    break;
                                case 3:
                                    Constant.allHiddenMediaList.set(Constant.renameFilePos,new File(path+name+extension));
                                    Constant.isFromRename=true;
                                    break;
                                case 4:
                                    Constant.allVideoInLocker.set(Constant.renameFilePos,new File(path+name+extension));
                                    Constant.isFromRename=true;
                                    break;
                                default:
                                    Constant.allMediaList.set(Constant.renameFilePos,new File(path+name+extension));
                                    Constant.allMediaList2.set(Constant.allMediaList2.indexOf(Constant.renameFile),
                                            Constant.allMediaList.get(Constant.renameFilePos));
                                    Constant.isFromRename=true;
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("mainSelectedFragment",String.valueOf(MainActivity.selectedFragmentValue));
                        finish();
                    }
                }
            }
        });
        //button ok listener above
    }

    public boolean check(String forbidden,String s){
        for(int i=0;i<s.length();i++){
            if(forbidden.contains(String.valueOf(s.charAt(i)))){
                return true;
            }
        }
        return false;
    }

}
