package com.example.learning;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.learning.Adapter.RVAdapterHidden;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Player extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private GestureDetector LeftGestureDetector,RightGestureDetector;
    final int MIN_DISTANCE=150;
    float x1,x2,y1,y2,specialX=-1,specialY=-1,specialXX=-1,specialYY=-1,curBrightness=0,forwardPar;
    public MediaPlayer mediaPlayer;
    public static final long DOUBLE_CLICK_TIME_INTERVAL = 700;
    public MediaController mediaController;
    public static long doubleTapTimeRight,doubleTapTimeLeft,duration,forwardBackwardConstant;
    public static int scrollSize,forwardSize;
    private Button ButtonLock;
    private RelativeLayout ProgressForBrightness;
    public LinearLayout linearLayoutForwardControl,linearLayoutBrightnessControl;
    public RelativeLayout linearLayoutVolumeControl; //just named it as linear layout for understanding smoothly
    VideoView VideoViewPLayer;
    AudioManager audioManager;
    View ViewRightSide,ViewLeftSide,ViewForLock;
    public int position=-1,stopPosition=-1,videoposition=0,maxVolume,curVolume,minVolume,curPosition;
    private SeekBar SeekBarVolume,SeekBarBrightness,SeekBarForwardBackward;
    private ImageView ImageViewSound,ImageViewBrightness,ImageViewForwardBackward;
    public static boolean isFromVolume=false, isFromBrightness=false, isFromForwardVolume=false,isFromForwardBrightness=false,
            isVolumeFirstTime=true,isForwardVolumeFirstTime=true,isBrightnessFirstTime=true,isForwardBrightnessFirstTime=true,
            isForwardVolumeScrollingFinished=false,isForwardBrightnessScrollingFinished=false,
            isFromDoubleTap = false,isFromHere=false,isFirstTime=true,isLocked=false,
            isFromSoundControl=false,isFromBrightnessControl=false,isFromForwardControl=false;
    public static int volumeCount=0,forwardVolumeCount=0,brightnessCount=0,forwardBrightnessCount=0,orientationValue;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RVAdapterHidden.isFromPlayerHidden=true;
        //getting display width and height for audio and brightness
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        scrollSize = height-400; //maximum scroll length in portrait .400 emnitei dichi
        forwardSize = width-100;
        int orientation = getResources().getConfiguration().orientation;
//        Toast.makeText(this, String.valueOf(scrollSize), Toast.LENGTH_SHORT).show();
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            scrollSize=width-200; //calculating maximum scroll length in landscape. 200 for same reason
            forwardSize = height-200;
            orientationValue=2;
        }
        else{
            orientationValue=1;
        }

        Toast.makeText(this, String.valueOf(forwardSize), Toast.LENGTH_SHORT).show();
        // measured display width and height for audio and brightness

        //lock button initialising
        ButtonLock = findViewById(R.id.ButtonLock);
//        ButtonLock.setBackgroundResource(R.drawable.ic_forward);
        //lock button initialised

        mediaController=new MediaController(this); //initialising media controller
        setContentView(R.layout.activity_player);

        // initialising audio manager
        audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        minVolume=audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        minVolume=0;
        // audio manager initialised above

        //volume progress initialising
        linearLayoutVolumeControl = findViewById(R.id.linearLayoutVolumeControl); //its a relative layout
        SeekBarVolume = findViewById(R.id.SeekBarVolume);
        ImageViewSound = findViewById(R.id.ImageViewSound);
        int setVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float setVolu = setVol*(100/(float)maxVolume);
        SeekBarVolume.setMax(200);
        SeekBarVolume.setProgress((int)setVolu);
        if((int)setVolu>0){
            ImageViewSound.setImageResource(R.drawable.ic_volume_up);
        }
        else{
            ImageViewSound.setImageResource(R.drawable.ic_volume_zero);
        }
        //volume progress initialised

        //brightness progress initialising
        linearLayoutBrightnessControl = findViewById(R.id.linearLayoutBrightnessControl);
        SeekBarBrightness = findViewById(R.id.SeekBarBrightness);
        ImageViewBrightness = findViewById(R.id.ImageViewBrightness);
        SeekBarBrightness.setMax(100);
        curBrightness = 7;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); // Get Params
        layoutParams.screenBrightness = .1f; // Set Value
        getWindow().setAttributes(layoutParams); // Set params
//        Toast.makeText(this, String.valueOf(curBrightness), Toast.LENGTH_SHORT).show();
        SeekBarBrightness.setProgress((int) curBrightness);
        //brightness progress initialised

        //forward backward after video initialising

        // view initialising
        ViewRightSide=findViewById(R.id.ViewRightSide);
        ViewLeftSide=findViewById(R.id.ViewLeftSide);
        ViewForLock = findViewById(R.id.ViewForLock);
        // view initialised

        //video starting
        VideoViewPLayer=findViewById(R.id.VideoViewPLayer);
        position=getIntent().getIntExtra("position",-1);
//        getSupportActionBar().hide();
        PlayVideo(mediaController); // starting video
        // video started

        //forward backward initialising
        linearLayoutForwardControl = findViewById(R.id.linearLayoutForwardControl);
        ImageViewForwardBackward = findViewById(R.id.ImageViewForwardBackward);
        SeekBarForwardBackward = findViewById(R.id.SeekBarForwardBackward);
        SeekBarForwardBackward.setMax(200);
        forwardBackwardConstant = (duration+1)/200;
        //forward backward initialised

        // initialising gesture detector
        LeftGestureDetector = new GestureDetector(this,new LeftGestureListener());
        RightGestureDetector = new GestureDetector(this,new RightGestureListener());
        ViewLeftSide.setOnTouchListener(touchListener);
        ViewRightSide.setOnTouchListener(touchListener2);
        // gesture detector initialised

        //checking if orientation changes during playing
        if(savedInstanceState!=null){
            int pos = savedInstanceState.getInt("savedPosition",0);
            Log.i("savedPosition",String.valueOf(pos));
            VideoViewPLayer.seekTo(pos);
            orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                orientationValue=2;
            }
            else{
                orientationValue=1;
            }
            Log.d("orientationValue",String.valueOf(orientationValue));
        }
        //checking if orientation changes during playing above
    }

    //button lock
    public void ButtonLockClicked(View view){
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        if(isLocked){
            ViewLeftSide.setVisibility(View.VISIBLE);
            ViewRightSide.setVisibility(View.VISIBLE);
            mediaController.setVisibility(View.VISIBLE);
            ViewForLock.setVisibility(View.INVISIBLE);
            isLocked=false;
            Toast.makeText(this, "unlocked", Toast.LENGTH_SHORT).show();
//            Drawable d = getResources().getDrawable(R.drawable.unlocked);
//            ButtonLock.setBackground(d);
        }
        else{
            ViewLeftSide.setVisibility(View.INVISIBLE);
            ViewRightSide.setVisibility(View.INVISIBLE);
            mediaController.setVisibility(View.INVISIBLE);
            ViewForLock.setVisibility(View.VISIBLE);
            isLocked=true;
            Toast.makeText(this, "locked", Toast.LENGTH_SHORT).show();
//            Drawable d = getResources().getDrawable(R.drawable.locked);
//            ButtonLock.setBackground(d);
        }
    }
    //button lock
    //
    final View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            return LeftGestureDetector.onTouchEvent(event);
        }
    };
    //

    //touch listener for left side below
    final View.OnTouchListener touchListener2 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            return RightGestureDetector.onTouchEvent(event);
        }
    };
    //touch listener for left side above
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void PlayVideo(final MediaController mediaController){
        mediaController.setAnchorView(VideoViewPLayer);
        int adapterFinder = getIntent().getIntExtra("adapterFinder",-1);
        String videoPath;
        if(adapterFinder==4){
            VideoViewPLayer.setMediaController(mediaController);
            videoPath = String.valueOf(Constant.allVideoInFolder.get(position));
        }
        else if(adapterFinder==2){
            VideoViewPLayer.setMediaController(mediaController);
            videoPath = String.valueOf(Constant.allHiddenMediaList.get(position));
        }
        else if(adapterFinder==3){
            VideoViewPLayer.setMediaController(mediaController);
            videoPath =String.valueOf(Constant.filteredMediaList.get(position));
        }
        else{
            VideoViewPLayer.setMediaController(mediaController);
            videoPath = String.valueOf(Constant.allMediaList.get(position));
        }
        try {
            VideoViewPLayer.setVideoPath(videoPath);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            duration = Long.parseLong(Objects.requireNonNull(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))); //in milli secs
            forwardPar = (float)(3*forwardSize)/((float)duration/1000); //for forward backward gesture
            VideoViewPLayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    try {
                        VideoViewPLayer.start();
                    } catch (Exception e) {
                        Toast.makeText(Player.this, "error", Toast.LENGTH_SHORT).show();
                    }
//                VideoViewPLayer.start();
//                duration = mediaPlayer.getDuration();
//                Toast.makeText(Player.this, "duration test +"+String.valueOf(duration), Toast.LENGTH_SHORT).show();
//                mediaPlayer.release();
//                mediaController.show();
                }
            });
        }
        catch (Exception e){
//            Toast.makeText(Player.this, "error", Toast.LENGTH_SHORT).show();
        }
//        VideoViewPLayer.setVideoPath(String.valueOf(Constant.allMediaList.get(position)));
//        duration = VideoViewPLayer.getDuration();
//        Toast.makeText(this, "duration"+String.valueOf(duration), Toast.LENGTH_SHORT).show();
    }
    public void OnBackPressed(){
        VideoViewPLayer.stopPlayback();
        super.onBackPressed();
    }

    //TODO HAVE TO ADD SCROLLING VOLUME :)
    //TODO HAVE TO ADD 10SEC FORWARD AND BACKWARD //DONE :)
    //TODO HAVE TO ADD ROTATE BUTTON IN PORTRAIT AND LANDSCAPE VIEW :)
    //TODO IMPLEMENT REAL FULLSCREEN VIEW . DONE :)

    public void OnPause(){
        try {
            VideoViewPLayer.pause();
        }
        catch (Exception ignored){

        }
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            VideoViewPLayer.start();
            VideoViewPLayer.seekTo(stopPosition);
            VideoViewPLayer.resume();
        }
        catch (Exception ignored){

        }
    }
    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
            stopPosition = VideoViewPLayer.getCurrentPosition();
            long videoPoss = VideoViewPLayer.getCurrentPosition();
            savedInstanceState.putInt("savedPosition", stopPosition);
            Log.d("savedPositionSavingLong",String.valueOf(videoPoss));
            Log.i("savedPositionSaving",String.valueOf(stopPosition));
    }

    //

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus){
            hideSystemUI(orientationValue);
//        }
    }

    //hiding system ui such as notification bar,navigation button etc
    @SuppressWarnings("deprecation")
    private void hideSystemUI(int check) {
        View decorView = getWindow().getDecorView();
        if(check==1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().setDecorFitsSystemWindows(false); //problem_may_rise
            }
            else {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().setDecorFitsSystemWindows(false); //problem_may_rise
            }
            else {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                // Hide the nav bar and status bar
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }
    //hiding system ui such as notification bar and navigation button etc above

    //custom toast left side ABOVE
    public void ShowToastLeft(int x, int y){
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.left_custom_toast,(ViewGroup)findViewById(R.id.LayoutToast));
        ImageView ToastImage=layout.findViewById(R.id.ToastImage);
        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP|Gravity.START,x,y);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    //custom toast left ABOVE
    //custom toast right BELOW
    public void ShowToastRight(int x,int y){
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.right_custom_toast,(ViewGroup)findViewById(R.id.LayoutToastRight));
        ImageView ToastImage=layout.findViewById(R.id.ToastImageRight);
        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP|Gravity.START,x,y);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    //custom toast right ABOVE
    //custom toast for sound
    public void ShowToastSound(int checker,int vol){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_sound,(ViewGroup)findViewById(R.id.LayoutToastSound));
        ImageView ImageViewSound = layout.findViewById(R.id.ImageViewSound);
        TextView TextViewSound = layout.findViewById(R.id.TextViewToastSound);
        if(checker==0){
            if(isFirstTime || !isFromHere) {
                ImageViewSound.setImageResource(R.drawable.ic_volume_zero);
                TextViewSound.setText("0");
                isFirstTime=false;
            }
            else{
                TextViewSound.setText("0");
            }
            isFromHere=true;
        }
        else{
            if(isFirstTime || isFromHere) {
                ImageViewSound.setImageResource(R.drawable.ic_volume_up);
            }
            TextViewSound.setText(String.valueOf(vol));
            isFromHere=false;
        }
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    } //TODO have to use textview i think
    //custom toast for sound

    class LeftGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            return true;
        }
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            int x= (int) e.getRawX();
            int y= (int) e.getRawY();
            doubleTapTimeLeft = System.currentTimeMillis();
            int pos = VideoViewPLayer.getCurrentPosition();
            VideoViewPLayer.seekTo(pos-10000);
            ShowToastLeft(x-160,y-160);
//            Log.i("TAG", String.valueOf(x)+" "+String.valueOf(y));
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            long singleTapTime = System.currentTimeMillis();
            if(singleTapTime-doubleTapTimeLeft>DOUBLE_CLICK_TIME_INTERVAL) {
                try {
                    if (mediaController.isShowing()) {
                        mediaController.hide();
                    }
                    else {
                        mediaController.show();
                    }
                    doubleTapTimeLeft = 0;
                }
                catch (Exception ignored){

                }
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //            ProgressForVolume.setVisibility(View.VISIBLE);


            if(specialXX!=e1.getX() || specialYY!=e1.getY()){
                curBrightness = SeekBarBrightness.getProgress();
                curPosition = VideoViewPLayer.getCurrentPosition();
//                Toast.makeText(Player.this, String.valueOf(curPosition), Toast.LENGTH_SHORT).show();
                specialXX = e1.getX();
                specialYY = e1.getY();
            }
            x1=e1.getX();
            y1=e1.getY();
            x2=e2.getX();
            y2=e2.getY();
            Log.i("(x1,y1),(x2,y2)","("+ x1 +","+ y1 +")"+" , "+
                    "("+ x2 +","+ y2 +")");
            if(Math.abs(specialXX-x2)<MIN_DISTANCE &&
                    !isFromSoundControl && !isFromForwardControl) { //!isFromForwardBrightness
                if(Math.abs(specialYY-y2)>MIN_DISTANCE || isFromBrightnessControl) {
                    if (!isBrightnessFirstTime) {
//                        SeekBarVolume.setVisibility(View.INVISIBLE);
//                        ImageViewSound.setVisibility(View.INVISIBLE);
                        linearLayoutVolumeControl.setVisibility(View.INVISIBLE);

//                        SeekBarForwardBackward.setVisibility(View.INVISIBLE);
//                        ImageViewForwardBackward.setVisibility(View.INVISIBLE);
                        linearLayoutForwardControl.setVisibility(View.INVISIBLE);

//                        SeekBarBrightness.setVisibility(View.VISIBLE);
//                        ImageViewBrightness.setVisibility(View.VISIBLE);
                        linearLayoutBrightnessControl.setVisibility(View.VISIBLE);

                        isFromBrightness = true;
                        isFromBrightnessControl = true;

                        mHandler.removeCallbacks(HideSeekBarBrightness);
                        mHandler.postDelayed(HideSeekBarBrightness, 500);

                    } else {
                        brightnessCount++;
                        if (brightnessCount == 5) {
                            isBrightnessFirstTime = false;
                            brightnessCount = 0;
                        }
                        isBrightnessFirstTime = false;
                    }

                    int valueY = (int) (y1 - y2);
                    float par = (float) scrollSize / 100;
                    int brightness = (int) (valueY / par) + (int) curBrightness;
                    if (brightness > 100) brightness = 100;
                    if (brightness < 0) brightness = 1;
                    ShowSeekBarBrightness(brightness);
                }
            }
            else if(Math.abs(specialYY-y2)<MIN_DISTANCE &&
                    !isFromBrightnessControl && !isFromSoundControl) { //!isFromBrightness
                if (Math.abs(specialXX - x2) > MIN_DISTANCE || isFromForwardControl) {
                    if (!isForwardBrightnessFirstTime) {

//                        SeekBarVolume.setVisibility(View.INVISIBLE);
//                        ImageViewSound.setVisibility(View.INVISIBLE);
                        linearLayoutVolumeControl.setVisibility(View.INVISIBLE);

//                        SeekBarBrightness.setVisibility(View.INVISIBLE);
//                        ImageViewBrightness.setVisibility(View.INVISIBLE);
                        linearLayoutBrightnessControl.setVisibility(View.INVISIBLE);

//                        SeekBarForwardBackward.setVisibility(View.VISIBLE);
//                        ImageViewForwardBackward.setVisibility(View.VISIBLE);
                        linearLayoutForwardControl.setVisibility(View.VISIBLE);

                        isFromForwardBrightness = true;
                        isFromForwardControl = true;

                        float pos = VideoViewPLayer.getCurrentPosition();
                        float progress = pos / forwardBackwardConstant;
                        SeekBarForwardBackward.setProgress((int) progress);

                        mHandler.removeCallbacks(SetDefaultValue);
                        mHandler.postDelayed(SetDefaultValue, 500);
                    } else {
                        forwardBrightnessCount++;
                        if (forwardBrightnessCount == 5) {
                            isForwardBrightnessFirstTime = false;
                            forwardBrightnessCount = 0;
                        }
                        isForwardBrightnessFirstTime = false;
                    }
                    int valueX = (int) (x2 - x1);
                    int pos = (int) ((valueX / forwardPar) * 1000);
                    pos += curPosition;
                    if (pos > duration) {
                        pos = (int) duration;
                    }
                    if (pos < 0) {
                        pos = 0;
                    }
//                Log.i("position  ",String.valueOf(curPosition)+" | "+String.valueOf(pos));
                    VideoViewPLayer.seekTo(pos);
                    if (valueX < 0) {
                        ShowForwardBackward(pos, 0);
                    } else {
                        ShowForwardBackward(pos, 1);
                    }
                }
            }
            return true;
        }
    }
    //effect for left part above

    //effect for right part
    class RightGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            return true;
        }

        //double tap
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            doubleTapTimeRight = System.currentTimeMillis();
            int x = (int) e.getRawX();
            int y = (int) e.getRawY();
            //getX() return value according to its parent but rawX() return value according to mobile screen
            ShowToastRight(x-160,y-160);
            int pos=VideoViewPLayer.getCurrentPosition();
            VideoViewPLayer.seekTo(pos+10000);
            isFromDoubleTap = true;
            return true;
        }
        //double tap above

        //onScroll effect
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(specialX!=e1.getX() || specialY!=e1.getY()){
                curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                curPosition = VideoViewPLayer.getCurrentPosition(); //in milli secs
                specialX = e1.getX();
                specialY = e1.getY();
            }
            x1=e1.getX();
            y1=e1.getY();
            x2=e2.getX();
            y2=e2.getY();
            Log.i("(x1,y1),(x2,y2)","("+ x1 +","+ y1 +")"+" , "+ "("+ x2 +","+ y2 +")");
            if(Math.abs(specialX-x2)<MIN_DISTANCE &&
                    !isFromForwardControl && !isFromBrightnessControl) { //volume part //!isFromForwardVolue
                if(Math.abs(specialY-y2)>MIN_DISTANCE || isFromSoundControl) {
                    if (!isVolumeFirstTime) {
//                        SeekBarBrightness.setVisibility(View.INVISIBLE);
//                        ImageViewBrightness.setVisibility(View.INVISIBLE);
                        linearLayoutBrightnessControl.setVisibility(View.INVISIBLE);

//                        SeekBarForwardBackward.setVisibility(View.INVISIBLE);
//                        ImageViewForwardBackward.setVisibility(View.INVISIBLE);
                        linearLayoutForwardControl.setVisibility(View.INVISIBLE);

//                        SeekBarVolume.setVisibility(View.VISIBLE);
//                        ImageViewSound.setVisibility(View.VISIBLE);
                        linearLayoutVolumeControl.setVisibility(View.VISIBLE);

                        isFromVolume = true;
                        isFromSoundControl = true;

                        mHandler.removeCallbacks(HideSeekBar);  //for volume
                        mHandler.postDelayed(HideSeekBar, 500);
                    } else {
//                    volumeCount++;
//                    if(volumeCount==5){
//                        isVolumeFirstTime=false;
//                        volumeCount=0;
//                    }
                        isVolumeFirstTime = false;
                        //TODO may have to change this
                    }

                    int valueY = (int) (y1 - y2);
                    float par = (float) scrollSize / maxVolume;
                    int vol = (int) (valueY / par) + curVolume;
                    if (vol > maxVolume) vol = maxVolume;
                    if (vol < 0) vol = 0;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
                    ShowSeekBarVolume(vol);
                }
            }
            else if((specialY-y2)<MIN_DISTANCE &&
                    !isFromSoundControl && !isFromBrightnessControl) {//!isFromVolume
                if(Math.abs(specialX-x2)>MIN_DISTANCE || isFromForwardControl) {
                    if (!isForwardVolumeFirstTime) {

                        linearLayoutVolumeControl.setVisibility(View.INVISIBLE);
                        linearLayoutBrightnessControl.setVisibility(View.INVISIBLE);
//                        SeekBarForwardBackward.setVisibility(View.VISIBLE);
//                        ImageViewForwardBackward.setVisibility(View.VISIBLE);
                        linearLayoutForwardControl.setVisibility(View.VISIBLE);

                        isFromForwardVolume = true;
                        isFromForwardControl = true;

                        float pos = VideoViewPLayer.getCurrentPosition();
                        float progress = pos / forwardBackwardConstant;
                        SeekBarForwardBackward.setProgress((int) progress);

                        mHandler.removeCallbacks(SetDefaultValue);
                        mHandler.postDelayed(SetDefaultValue, 500);
                    } else {
                        forwardVolumeCount++;
                        if (forwardVolumeCount == 5) {
                            isForwardVolumeFirstTime = false;
                            forwardVolumeCount = 0;
                        }
                        isForwardVolumeFirstTime = false;
                    }

                    int valueX = (int) (x2 - x1);
                    int pos = (int) ((valueX / forwardPar) * 1000);
                    pos += curPosition;
//                Log.i("fowardPar + pos",String.valueOf(forwardPar)+" | "+String.valueOf(pos));
                    if (pos > duration) {
                        pos = (int) duration;
                    }
                    if (pos < 0) {
                        pos = 0;
                    }
                    Log.i("position:  ", curPosition + " | " + pos);
//                SeekPosition(pos);
                    VideoViewPLayer.seekTo(pos);
                    if (valueX < 0) {
                        ShowForwardBackward(pos, 0);
                    } else {
                        ShowForwardBackward(pos, 1);
                    }
                }
            }
            return true;
        }
        //onScroll effect above

        //single tap confirmed
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            long singleTapTime = System.currentTimeMillis();
            if(singleTapTime-doubleTapTimeRight>DOUBLE_CLICK_TIME_INTERVAL) {
                try {
                    if (mediaController.isShowing()) {
                        mediaController.hide();
                    } else {
                        mediaController.show();
                        doubleTapTimeRight = 0;
                    }
                }
                catch (Exception ignored){

                }
            }
//            Log.i("all: ","\n"+String.valueOf(isFromVolume)+"\n"+String.valueOf(isFromBrightness)+"\n"+
//                    String.valueOf(isFromForwardVolume)+"\n"+String.valueOf(isFromForwardBrightness)+"\n"+
//                    String.valueOf(isVolumeFirstTime)+"\n"+String.valueOf(isForwardVolumeFirstTime)+"\n"+
//                    String.valueOf(isBrightnessFirstTime)+"\n"+String.valueOf(isForwardBrightnessFirstTime));
            return true;
        }
        // single tap confirmed above
    }
    //effect for right part above

    //this will show seek bar for volume
    public void ShowSeekBarVolume(int progress){
//        SeekBarVolume.setVisibility(View.VISIBLE);
        float each = 200/(float)maxVolume;
        float prog = progress*each;
        progress = (int) Math.ceil(prog);
        if(progress>0){
            ImageViewSound.setImageResource(R.drawable.ic_volume_up);
        }
        else{
            ImageViewSound.setImageResource(R.drawable.ic_volume_zero);
        }
        SeekBarVolume.setProgress(progress);
//        isFromVolume=false;
//        isVolumeFirstTime=true;
//        isFromForwardVolume=false;
//        SeekBarVolume.setVisibility(View.INVISIBLE);
    }
    //seek bar volume above

    // hide seek bar volume
    public final Runnable HideSeekBar = new Runnable() {
        @Override
        public void run() {
//            ProgressForVolume.setVisibility(View.INVISIBLE);
//            SeekBarVolume.setVisibility(View.INVISIBLE);
//            ImageViewSound.setVisibility(View.INVISIBLE);
            linearLayoutVolumeControl.setVisibility(View.INVISIBLE);
            isFromVolume=false;
            isVolumeFirstTime=true;
            SetEverythingDefault();
        }
    };
    // hide seek bar volume above

    //show seek bar for brightness
    public void ShowSeekBarBrightness(int progress){
        float BackLightValue = ((float)progress)/100;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); // Get Params
        layoutParams.screenBrightness = BackLightValue; // Set Value
        getWindow().setAttributes(layoutParams); // Set params
        SeekBarBrightness.setProgress(progress);
        ImageViewBrightness.setImageResource(R.drawable.ic_brightness);
    }
    //show seek bar for brightness above

    //hide seek bar brightness
    public final Runnable HideSeekBarBrightness = new Runnable() {
        @Override
        public void run() {
//            SeekBarBrightness.setVisibility(View.INVISIBLE);
//            ImageViewBrightness.setVisibility(View.INVISIBLE);
            linearLayoutBrightnessControl.setVisibility(View.INVISIBLE);
            isBrightnessFirstTime=true;
            isFromBrightness=false;
            SetEverythingDefault();
        }
    };
    //hide seek bar brightness

    //show forward backward
    public void ShowForwardBackward(int pos,int checker){
//        int poss = VideoViewPLayer.getCurrentPosition();
//        VideoViewPLayer.seekTo(pos);
        float progress = (float) pos/forwardBackwardConstant;
        if(checker==1){
            ImageViewForwardBackward.setImageResource(R.drawable.ic_forward2);
        }
        else{
            ImageViewForwardBackward.setImageResource(R.drawable.ic_backward2);
        }
        SeekBarForwardBackward.setProgress((int)progress);
//        Toast.makeText(this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
    }
    //seek video position

    //runnable for forward backward
    public final Runnable SetDefaultValue = new Runnable() {
        @Override
        public void run() {
            isFromForwardVolume=false;
            isFromForwardBrightness=false;
            isForwardVolumeFirstTime=true;
            isForwardBrightnessFirstTime=true;
            isVolumeFirstTime=true;
            isBrightnessFirstTime=true;
//            isForwardVolumeScrollingFinished=true;
//            isForwardBrightnessScrollingFinished=true;
            linearLayoutForwardControl.setVisibility(View.INVISIBLE);
            SetEverythingDefault();
//            SeekBarForwardBackward.setVisibility(View.INVISIBLE);
//            ImageViewForwardBackward.setVisibility(View.INVISIBLE);

        }
    };
    //runnable for forward backward done

    //set everything default
    public void SetEverythingDefault(){
        isFromVolume=false;
        isFromBrightness=false;
        isFromForwardVolume=false;
        isFromForwardBrightness=false;
        isVolumeFirstTime=true;
        isForwardVolumeFirstTime=true;
        isBrightnessFirstTime=true;
        isForwardBrightnessFirstTime=true;
        isForwardVolumeScrollingFinished=false;
        isForwardBrightnessScrollingFinished=false;

        isFromSoundControl=false;
        isFromBrightnessControl=false;
        isFromForwardControl=false;

    }
    //set everything default above

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientationValue=2;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            orientationValue=1;
        }
    }

    
}
