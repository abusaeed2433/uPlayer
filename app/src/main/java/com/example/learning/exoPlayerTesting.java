package com.example.learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learning.Adapter.RVAdapterHidden;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.Objects;


public class exoPlayerTesting extends AppCompatActivity {
    PlayerView playerView;
    ImageView imageViewRotateButton,imageViewLockButton,imageViewPause,imageViewUnlock,
            imageViewFullScreen,imageViewPrevious,imageViewNext,imageViewExitPlayer,
            imageViewCaption;
    SubtitleView subtitleView;
    SimpleExoPlayer simpleExoPlayer;
    LinearLayout bottomLinearLayout,lastLinearLayout,linearLayoutNameAndBack;
    TextView textViewPlayBackSpeed,textViewFileName;
    public int position2;
    public boolean orientationFlag=true,isLocked,isPlaying;
    public int fullScreenValue=0,sizeAccordingAdapter;
    public String formattedDuration="00:00:00";
    public static long curPos;
    public MediaSource mediaSource;
    //from player
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private GestureDetector LeftGestureDetector,RightGestureDetector;
    final int MIN_DISTANCE=75;
    float x1,x2,y1,y2,specialX=-1,specialY=-1,specialXX=-1,specialYY=-1,curBrightness=0;
    public static final long DOUBLE_CLICK_TIME_INTERVAL = 700;
    public static long doubleTapTimeRight,doubleTapTimeLeft,duration,scrollSize;
    public float forwardBackwardConstant;
    public static int forwardSize,adapterFinder;
//    private Button ButtonLock;
//    private RelativeLayout ProgressForBrightness;
    public LinearLayout linearLayoutForwardControl,linearLayoutBrightnessControl;
    public RelativeLayout linearLayoutVolumeControl; //just named it as linear layout for understanding smoothly
    AudioManager audioManager;
    public View ViewRightSide,ViewLeftSide,ViewForLock;
    public int maxVolume,curVolume,minVolume,curPosition;
    private SeekBar SeekBarVolume,SeekBarBrightness,SeekBarForwardBackward;
    private ImageView ImageViewSound,ImageViewBrightness;
    private TextView ImageViewForwardBackward;
    public static boolean isFromVolume=false, isFromBrightness=false, isFromForwardVolume=false,isFromForwardBrightness=false,
            isVolumeFirstTime=true,isForwardVolumeFirstTime=true,isBrightnessFirstTime=true,isForwardBrightnessFirstTime=true,
            isForwardVolumeScrollingFinished=false,isForwardBrightnessScrollingFinished=false,
            isFromDoubleTap = false,isFromHere=false,isFirstTime=true,
            isFromSoundControl=false,isFromBrightnessControl=false,isFromForwardControl=false,isDurationTaken=false;
    public static int forwardVolumeCount=0,brightnessCount=0,forwardBrightnessCount=0,orientationValue;
    public static boolean specialBool=false;
    public Uri specialUri;
    Toast mainToast;
    //from player
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player_testing);
        playerView = findViewById(R.id.player_view);
//        btFullScreen = findViewById(R.id.bt_fullscreen);


        //todo specailBool is for opening from file manager directly :( not working till now

        //custom controller
        isLocked=false;
        fullScreenValue = 0;
        imageViewRotateButton = findViewById(R.id.imageViewRotateButton);
        imageViewLockButton = findViewById(R.id.imageViewLockButton);
        imageViewUnlock = findViewById(R.id.imageViewUnlock);
        bottomLinearLayout = findViewById(R.id.bottomLinearLayout);
        lastLinearLayout = findViewById(R.id.lastLinearLayout);
        linearLayoutNameAndBack = findViewById(R.id.linearLayoutNameAndBack);
        textViewPlayBackSpeed = findViewById(R.id.textViewPlayBackSpeed);
        imageViewPause = findViewById(R.id.imageViewPause);
        imageViewFullScreen = findViewById(R.id.imageViewFullScreen);
        imageViewPrevious = findViewById(R.id.imageViewPrevious);
        imageViewNext = findViewById(R.id.imageViewNext);
        imageViewExitPlayer = findViewById(R.id.imageViewExitPlayer);
        imageViewCaption = findViewById(R.id.imageViewCaption);
        textViewFileName = findViewById(R.id.textViewFileName);

        //subtitle
        subtitleView = findViewById(R.id.exo_subtitles);
        //subtitle above
        //custom controller above

        if(specialBool) {
            position2 = getIntent().getIntExtra("position", 0);
        }
        else {
            // getting path when opening from file manager
            try {
                specialUri = getIntent().getData();
                if(specialUri!=null) {
                    textViewFileName.setText(getSpecialFileName(specialUri));
                }
                else{
                    textViewFileName.setText(getString(R.string.file_name_here));
                }
            } catch (Exception ignored) {

            }
            //getting path when opening from file manager above
        }

        //just hiding the top status bar and below navigation bar temporary
        hideNavigationAndStatusBar(getWindow());

        adapterFinder = getIntent().getIntExtra("adapterFinder",-1);

        switch (adapterFinder){
            case 2:
                sizeAccordingAdapter = Constant.allHiddenMediaList.size();
                break;
            case 3:
                sizeAccordingAdapter = Constant.filteredMediaList.size();
                break;
            case 4:
                sizeAccordingAdapter = Constant.allVideoInFolder.size();
                break;
            case 7:
                sizeAccordingAdapter = Constant.allVideoInLocker.size();
                break;
            default:
                sizeAccordingAdapter = Constant.allMediaList.size();
                break;
        }

        if(specialBool) {
            if (position2 == 0) {
                imageViewPrevious.setImageResource(R.drawable.ic_skip_previous_disable);
            }
            if (position2 == sizeAccordingAdapter - 1) {
                imageViewNext.setImageResource(R.drawable.ic_skip_next_disable);
            }
            final String filePath = getPathAndSetName(adapterFinder, position2);
            initializePlayer(filePath);
        }
        else{
            specialInitialize(specialUri);
            imageViewPrevious.setImageResource(R.drawable.ic_skip_previous_disable);
            imageViewNext.setImageResource(R.drawable.ic_skip_next_disable);
        }
//        buildMediaSource(filePath);

        //checking orientation mode
        int orientation = getResources().getConfiguration().orientation;

        //checking orientation mode above
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                if(playbackState == simpleExoPlayer.STATE_READY){
                    if(!isDurationTaken) {
                        duration = simpleExoPlayer.getDuration();
                        Log.d("durationValue1",String.valueOf(duration));
                        forwardBackwardConstant = (float) Math.ceil((float)(duration + 1)/(2*scrollSize));
                        new Handler(Looper.getMainLooper()).postDelayed(makeViewVisible,200);
                        formattedDuration="/"+formatTime(duration);
                        isDurationTaken = true;
                    }
                }
            }
        });



        //rotate button touch listener
        imageViewRotateButton.setOnClickListener(view -> {
            if(orientationFlag){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            orientationFlag=!orientationFlag;
        });

        imageViewLockButton.setOnClickListener(view -> {
            isLocked=true;
            showOnlyLock();
        });

        imageViewUnlock.setOnClickListener(view -> {
            isLocked=false;
            showController();
        });

        textViewPlayBackSpeed.setOnClickListener(view -> {
            PlaybackParameters param;
            switch (textViewPlayBackSpeed.getText().toString()){
                case "1.0X":
                    textViewPlayBackSpeed.setText(getString(R.string.just_specifier,"1.5X"));
                    param = new PlaybackParameters((float)1.5);
                    break;
                case "1.5X":
                    textViewPlayBackSpeed.setText(getString(R.string.just_specifier,"2.0X"));
                    param = new PlaybackParameters((float)2);
                    break;
                case "2.0X":
                    textViewPlayBackSpeed.setText(getString(R.string.just_specifier,".25X"));
                    param = new PlaybackParameters((float).25);
                    break;
                case ".25X":
                    textViewPlayBackSpeed.setText(getString(R.string.just_specifier,".5X"));
                    param = new PlaybackParameters((float).5);
                    break;
                case ".5X":
                    textViewPlayBackSpeed.setText(getString(R.string.just_specifier,"1.0X"));
                    param = new PlaybackParameters((float)1.0);
                    break;
                default:
                    textViewPlayBackSpeed.setText(getString(R.string.just_specifier,"1.0X"));
                    param = new PlaybackParameters((float)1);
                    break;
            }
            simpleExoPlayer.setPlaybackParameters(param);
        });

        imageViewPause.setOnClickListener(view -> {
            if(simpleExoPlayer.isPlaying()){
                imageViewPause.setImageResource(R.drawable.ic_play);
                simpleExoPlayer.setPlayWhenReady(false);
            }
            else{
                imageViewPause.setImageResource(R.drawable.ic_pause);
                simpleExoPlayer.setPlayWhenReady(true);
            }
        });

        imageViewFullScreen.setOnClickListener(view -> {
            switch (fullScreenValue){
                case 0:
                    imageViewFullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    fullScreenValue=1;
                    hideNavigationAndStatusBar(getWindow());
                    break;
                case 1:
                    imageViewFullScreen.setImageResource(R.drawable.ic_crop);
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    fullScreenValue=2;
                    hideNavigationAndStatusBar(getWindow());
                    break;
                case 2:
                    imageViewFullScreen.setImageResource(R.drawable.ic_fullscreen);
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    fullScreenValue=0;
                    break;
                default:
                    fullScreenValue=0;
                    break;
            }
        });

        //imageView Previous touch listener
        imageViewPrevious.setOnClickListener(view -> {
            if(position2>0 && specialBool)
            {
                position2--;
                if(position2==0){
                    imageViewPrevious.setImageResource(R.drawable.ic_skip_previous_disable);
                }
                if(position2==sizeAccordingAdapter-2){
                    imageViewNext.setImageResource(R.drawable.ic_skip_next);
                }
                String filePath2 = getPathAndSetName(adapterFinder,position2);
                Log.d("imageViewPrev",filePath2);
                simpleExoPlayer.setPlayWhenReady(false);
                simpleExoPlayer.release();
                simpleExoPlayer = null;
                getDuration(filePath2);
                initializePlayer(filePath2);
            }
        });
        //imageView Previous touch listener above

        //imageView Next touch listener
        imageViewNext.setOnClickListener(view -> {

            if(position2<sizeAccordingAdapter-1 && specialBool)
            {
                position2++;
                if(position2==sizeAccordingAdapter-1){
                    imageViewNext.setImageResource(R.drawable.ic_skip_next_disable);
                }
                if(position2==1){
                    imageViewPrevious.setImageResource(R.drawable.ic_previous);
                }

                String filePath2 = getPathAndSetName(adapterFinder,position2);
                Log.d("imageViewNext",filePath2);
                simpleExoPlayer.setPlayWhenReady(false);
                simpleExoPlayer.release();
                simpleExoPlayer = null;
                getDuration(filePath2);
                initializePlayer(filePath2);
            }
        });//TODO too much problem. needs to be solved :) solved I hope
        //imageView Next touch listener above

        //imageView Exit player touch listener
        imageViewExitPlayer.setOnClickListener(view -> {
            specialBool = false;
            exoPlayerTesting.super.onBackPressed();
            mHandler.removeCallbacks(makeViewVisible);
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.release();
        });
        //imageView Exit player touch listener above

        //imageViewCaption above
        imageViewCaption.setOnClickListener(view -> {
            curPos = simpleExoPlayer.getCurrentPosition();
            final AlertDialog alertDialog = new AlertDialog.Builder(exoPlayerTesting.this).create();
            alertDialog.setTitle("add subtitle");
            alertDialog.setMessage("Do you want to add subtitle?");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "yes", (dialogInterface, i) -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent,1);
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "no", (dialogInterface, i) -> alertDialog.cancel());
            alertDialog.show();
        });
        //imageViewCaption listener above

        //from player activity
        RVAdapterHidden.isFromPlayerHidden=true;
        //getting display width and height for audio and brightness
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        scrollSize = height-400; //maximum scroll length in portrait .400 emnitei dichi
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            scrollSize=width-200; //calculating maximum scroll length in landscape. 200 for same reason
            //forwardSize = height-200;
            orientationValue=2;
        }
        else{
            orientationValue=1;
        }

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


        //forward backward initialising
        linearLayoutForwardControl = findViewById(R.id.linearLayoutForwardControl);
        ImageViewForwardBackward = findViewById(R.id.ImageViewForwardBackward);
        SeekBarForwardBackward = findViewById(R.id.SeekBarForwardBackward);
        SeekBarForwardBackward.setMax(200);
        forwardBackwardConstant = (float)500;
        isDurationTaken=false;
//        forwardBackwardConstant = (duration+1)/200; initialised later
        Log.d("videoDuration+constant",duration+" | "+forwardBackwardConstant);

        //forward backward initialised

        // initialising gesture detector
        LeftGestureDetector = new GestureDetector(this,new LeftGestureListener());
        RightGestureDetector = new GestureDetector(this,new RightGestureListener());
        ViewLeftSide.setOnTouchListener(touchListener);
        ViewRightSide.setOnTouchListener(touchListener2);
        // gesture detector initialised

        //checking if orientation changes during playing
        if(savedInstanceState!=null){
            long pos = savedInstanceState.getLong("savedPosition",0);
            simpleExoPlayer.seekTo(pos);
            orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){ orientationValue=2; }
            else{ orientationValue=1;}
        }
        //checking if orientation changes during playing above
        //from player activity above

        mainToast = new Toast(this);
    }


    private void hideNavigationAndStatusBar(@Nullable Window window) {
        if(window == null) return;

        View decorView = window.getDecorView();
        if(decorView!=null){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    //initialising player
    private void initializePlayer(String filePath) {
        Log.d("filePath",filePath);
        Uri videoUrl = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID +".provider",
                new File(filePath));

        Log.d("filePathLocation",filePath);
        simpleExoPlayer=null;
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        DataSpec dataSpec = new DataSpec(videoUrl);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        buildMediaSourceNew(videoUrl);

        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    private void specialInitialize(Uri videoUrl) {
        simpleExoPlayer=null;
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        DataSpec dataSpec = new DataSpec(videoUrl);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }
        //factory was never used so I removed it temporary
        buildMediaSourceNew(videoUrl);

        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.setPlayWhenReady(true);
    }
    //special initialise

    private void buildMediaSourceNew(Uri uri){
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,"exoplayer"));

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory,extractorsFactory)
                .createMediaSource(MediaItem.fromUri(uri));
        simpleExoPlayer.prepare(mediaSource);
    }


    /*public void buildSubtitleSource(Uri uri){
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,"exoplayer"));
        Format subtitleFormat = Format.createTextSampleFormat(
                null,
                MimeTypes.APPLICATION_SUBRIP,
                null,
                Format.NO_VALUE,
                Format.NO_VALUE,
                "en",
                null,Format.OFFSET_SAMPLE_RELATIVE);
        MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri,subtitleFormat,C.TIME_UNSET);
        MergingMediaSource mergedSource =
                new MergingMediaSource(mediaSource, subtitleSource);
        simpleExoPlayer.prepare(mergedSource);
    }*/


    //others part below


    @Override
    public void onBackPressed() {
        mainToast.cancel();
        specialBool=false;
        super.onBackPressed();
        mHandler.removeCallbacks(makeViewVisible);
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.release();
    }

    //onSaveInstance for saving video position

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long curPosition = simpleExoPlayer.getCurrentPosition();
        outState.putLong("savedPosition",curPosition);
    }
    //onSaveInstance for saving video position above


    //onStop
    @Override
    protected void onStop() {
        super.onStop();
//        mLastPosition = simpleExoPlayer.getCurrentPosition();
        simpleExoPlayer.setPlayWhenReady(false);
    }

    //onStop above

    @Override
    protected void onPause() {
        super.onPause();
        isPlaying = simpleExoPlayer.isPlaying();
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isPlaying) {
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.getPlaybackState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isPlaying){
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.getPlaybackState();
        }
    }

    //player
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
    }
    //seek bar volume above

    // hide seek bar volume
    public final Runnable HideSeekBar = new Runnable() {
        @Override
        public void run() {
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

    //hide seek bar brightness
    public final Runnable HideSeekBarBrightness = new Runnable() {
        @Override
        public void run() {
            linearLayoutBrightnessControl.setVisibility(View.INVISIBLE);
            isBrightnessFirstTime=true;
            isFromBrightness=false;
            SetEverythingDefault();
        }
    };
    //hide seek bar brightness

    public String formatTime(long time){

        long val = time/1000;
        int hour =(int) val/3600;
        int min =(int) (val-(hour*3600))/60;
        int sec =(int) val-(hour*3600)-min*60;

        String mainPart="";

        if(hour!=0) {
            if (String.valueOf(hour).length() == 1) mainPart += "0" + hour+":";
            else mainPart += hour+":";
        }

        if (String.valueOf(min).length() == 1)mainPart += "0" + min +":";
        else mainPart += min +":";

        if(String.valueOf(sec).length()==1) mainPart+="0"+ sec;
        else mainPart+=String.valueOf(sec);

        return mainPart;
    }

    //show forward backward
    public void ShowForwardBackward(long pos){
        Log.d("durationValue",String.valueOf(duration));
        float progress = (200*pos)/(float)duration;
        Log.d("progressFunc",String.valueOf(progress));

        String formattedCur = formatTime(pos);

        //speed = %1$s in string.xml
        ImageViewForwardBackward.setText(getString(R.string.just_specifier,formattedCur+formattedDuration));

        Log.d("progress", pos +" | "+
                duration +" | "+
                progress);
        SeekBarForwardBackward.setProgress((int)Math.ceil(progress));
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

            linearLayoutForwardControl.setVisibility(View.INVISIBLE);

            SetEverythingDefault();

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

    //player above

    //listener
    class LeftGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            return !isLocked;
        }
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            int x= (int) e.getRawX();
            int y= (int) e.getRawY();
            doubleTapTimeLeft = System.currentTimeMillis();
//            int pos = VideoViewPLayer.getCurrentPosition();
            long pos = simpleExoPlayer.getCurrentPosition();
//            VideoViewPLayer.seekTo(pos-10000);
            long uPos = pos-10000;
            if(uPos<0) uPos=0;
            simpleExoPlayer.seekTo(uPos);
            ShowToastLeft(x-160,y-160);
//            Log.i("TAG", String.valueOf(x)+" "+String.valueOf(y));
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            long singleTapTime = System.currentTimeMillis();
            if(singleTapTime-doubleTapTimeLeft>DOUBLE_CLICK_TIME_INTERVAL) {
                Log.d("position", e.getRawX() +" | "+ e.getRawY());
                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                Log.d("width+height", width +" | "+ height);
                if(playerView.isControllerVisible()){
                    playerView.hideController();
                    ViewLeftSide.setVisibility(View.VISIBLE);
                    ViewRightSide.setVisibility(View.VISIBLE);
                }
                else {
                    playerView.showController();
                    ViewLeftSide.setVisibility(View.INVISIBLE);
                    ViewRightSide.setVisibility(View.INVISIBLE);
                    mHandler.removeCallbacks(makeViewVisible);
                    mHandler.postDelayed(makeViewVisible,500);
                }
            }
            float pos = (float)simpleExoPlayer.getCurrentPosition();
            Log.d("currentPosition2",String.valueOf(pos));
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //            ProgressForVolume.setVisibility(View.VISIBLE);


            if(specialXX!=e1.getX() || specialYY!=e1.getY()){
                curBrightness = SeekBarBrightness.getProgress();
//                curPosition = VideoViewPLayer.getCurrentPosition();
                curPosition =(int) simpleExoPlayer.getCurrentPosition();
                Log.d("curPosition",String.valueOf(curPosition));
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
                        linearLayoutVolumeControl.setVisibility(View.INVISIBLE);
                        linearLayoutForwardControl.setVisibility(View.INVISIBLE);
                        linearLayoutBrightnessControl.setVisibility(View.VISIBLE);

                        isFromBrightness = true;
                        isFromBrightnessControl = true;

                        mHandler.removeCallbacks(HideSeekBarBrightness);
                        mHandler.postDelayed(HideSeekBarBrightness, 500);

                    } else {
                        brightnessCount++;
                        if (brightnessCount == 5) {
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
                        linearLayoutVolumeControl.setVisibility(View.INVISIBLE);
                        linearLayoutBrightnessControl.setVisibility(View.INVISIBLE);
                        linearLayoutForwardControl.setVisibility(View.VISIBLE);

                        isFromForwardBrightness = true;
                        isFromForwardControl = true;

//                        float pos = VideoViewPLayer.getCurrentPosition();
                        float pos = (float)simpleExoPlayer.getCurrentPosition();
                        float progress = (pos*200)/duration;
                        Log.d("progress", pos +" | "+
                                duration +" | "+
                                progress);
                        SeekBarForwardBackward.setProgress((int)Math.ceil(progress));

                        mHandler.removeCallbacks(SetDefaultValue);
                        mHandler.postDelayed(SetDefaultValue, 750);
                    } else {
                        forwardBrightnessCount++;
                        if (forwardBrightnessCount == 5) {
                            forwardBrightnessCount = 0;
                        }
                        isForwardBrightnessFirstTime = false;
                    }
                    int valueX = (int) (x2 - x1);
                    long pos = (long) valueX *(int)forwardBackwardConstant;
                    Log.d("mainPos",String.valueOf(pos));
                    pos += curPosition;
                    if (pos > duration) {
                        pos = (int)duration;
                    }
                    if (pos < 0) {
                        pos = 0;
                    }
                    simpleExoPlayer.seekTo(pos);
                    Log.d("seekTo(pos)", valueX +" | "+ pos);
                    ShowForwardBackward(pos);
                }
            }
            return true;
        }
    }

    public void ShowToastLeft(int x, int y){
        mainToast.cancel();
        //showing new toast
        mainToast = new Toast(exoPlayerTesting.this);
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.left_custom_toast,(ViewGroup)findViewById(R.id.LayoutToast));
        mainToast.setGravity(Gravity.TOP|Gravity.START,x,y);
        mainToast.setDuration(Toast.LENGTH_SHORT);
        mainToast.setView(layout);
        mainToast.show();
    }
    //custom toast left ABOVE

    //custom toast right BELOW
    public void ShowToastRight(int x,int y){
        mainToast.cancel();
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.right_custom_toast,(ViewGroup)findViewById(R.id.LayoutToastRight));

        mainToast=new Toast(getApplicationContext());
        mainToast.setGravity(Gravity.TOP|Gravity.START,x,y);
        mainToast.setDuration(Toast.LENGTH_SHORT);
        mainToast.setView(layout);
        mainToast.show();
    }
    //custom toast right ABOVE

    //effect for right part
    class RightGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");
            return !isLocked;
        }

        //double tap
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            doubleTapTimeRight = System.currentTimeMillis();
            int x = (int) e.getRawX();
            int y = (int) e.getRawY();
            //getX() return value according to its parent but rawX() return value according to mobile screen
            ShowToastRight(x-160,y-160);
            long pos = simpleExoPlayer.getCurrentPosition();
            long uPos=pos+10000;
            if(uPos>duration) uPos=duration;

            simpleExoPlayer.seekTo(uPos);
            isFromDoubleTap = true;
            return true;
        }
        //double tap above

        //onScroll effect
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(specialX!=e1.getX() || specialY!=e1.getY()){
                curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                curPosition = (int)simpleExoPlayer.getCurrentPosition();
                Log.d("curr2Position",String.valueOf(curPosition));
                specialX = e1.getX();
                specialY = e1.getY();
            }
            x1=e1.getX();
            y1=e1.getY();
            x2=e2.getX();
            y2=e2.getY();
            Log.d("scrolledSize",String.valueOf(x2-x1));
            if(Math.abs(specialX-x2)<MIN_DISTANCE &&
                    !isFromForwardControl && !isFromBrightnessControl) { //volume part //!isFromForwardVolue
                if(Math.abs(specialY-y2)>MIN_DISTANCE || isFromSoundControl) {
                    if (!isVolumeFirstTime) {
                        linearLayoutBrightnessControl.setVisibility(View.INVISIBLE);

                        linearLayoutForwardControl.setVisibility(View.INVISIBLE);

                        linearLayoutVolumeControl.setVisibility(View.VISIBLE);

                        isFromVolume = true;
                        isFromSoundControl = true;

                        mHandler.removeCallbacks(HideSeekBar);  //for volume
                        mHandler.postDelayed(HideSeekBar, 500);
                    } else {
                        isVolumeFirstTime = false;
                        //TODO may have to change this :) solved I hope
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
                        linearLayoutForwardControl.setVisibility(View.VISIBLE);

                        isFromForwardVolume = true;
                        isFromForwardControl = true;

                        float pos = (float)simpleExoPlayer.getCurrentPosition();
                        Log.d("Curr3Position",String.valueOf(pos));
                        float progress = (pos*200)/duration;
                        Log.d("progress", pos +" | "+
                                duration +" | "+
                                progress);
                        SeekBarForwardBackward.setProgress((int)Math.ceil(progress));

                        mHandler.removeCallbacks(SetDefaultValue);
                        mHandler.postDelayed(SetDefaultValue, 750);
                    } else {
                        forwardVolumeCount++;
                        if (forwardVolumeCount == 5) {
                            isForwardVolumeFirstTime = false;
                            forwardVolumeCount = 0;
                        }
                        isForwardVolumeFirstTime = false;
                    }
                    int valueX = (int) (x2 - x1);
                    long pos = (long) valueX *(int)forwardBackwardConstant;
                    Log.d("mainPos",String.valueOf(pos));

                    pos += curPosition;
                    if (pos > duration) {
                        pos = (int) duration;
                    }
                    if (pos < 0) {
                        pos = 0;
                    }
                    Log.i("position:  ", curPosition + " | " + pos);

                    simpleExoPlayer.seekTo(pos);
                    if (valueX < 0) {
                        ShowForwardBackward(pos);
                    } else {
                        ShowForwardBackward(pos);
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
                if(playerView.isControllerVisible()){
                    playerView.hideController();
                }
                else {
                    playerView.showController();
                    ViewLeftSide.setVisibility(View.INVISIBLE);
                    ViewRightSide.setVisibility(View.INVISIBLE);
                    mHandler.removeCallbacks(makeViewVisible);
                    mHandler.postDelayed(makeViewVisible,500);
                }
                Log.d("duration+Constant", duration +" | "+ forwardBackwardConstant);
            }
            return true;
        }
        // single tap confirmed above
    }
    //effect for right part above
    //listener above
    public final Runnable makeViewVisible = new Runnable() {
        @Override
        public void run() {
            if(!playerView.isControllerVisible()) {
                Log.d("visibilityBool",String.valueOf(playerView.isControllerVisible()));
                ViewLeftSide.setVisibility(View.VISIBLE);
                ViewRightSide.setVisibility(View.VISIBLE);
            }
            else{
                Log.d("visibilityBool",String.valueOf(playerView.isControllerVisible()));
                mHandler.removeCallbacks(makeViewVisible);  //when back pressed handler keeps running. this line prevents that
                                                            //don't know how
                mHandler.postDelayed(makeViewVisible,500);
            }
        }
    };

    //this will hide everything in controller except lock button
    public void showOnlyLock(){
        imageViewRotateButton.setVisibility(View.INVISIBLE);
        textViewPlayBackSpeed.setVisibility(View.INVISIBLE);
        bottomLinearLayout.setVisibility(View.INVISIBLE);
        lastLinearLayout.setVisibility(View.INVISIBLE);
        linearLayoutNameAndBack.setVisibility(View.INVISIBLE);
        imageViewCaption.setVisibility(View.INVISIBLE);
        imageViewUnlock.setVisibility(View.VISIBLE);
    }
    //this will hide everything in controller except lock button above

    //this will show(restore) everything in controller
    public void showController(){
        imageViewRotateButton.setVisibility(View.VISIBLE);
        textViewPlayBackSpeed.setVisibility(View.VISIBLE);
        bottomLinearLayout.setVisibility(View.VISIBLE);
        lastLinearLayout.setVisibility(View.VISIBLE);
        linearLayoutNameAndBack.setVisibility(View.VISIBLE);
        imageViewCaption.setVisibility(View.VISIBLE);
        imageViewUnlock.setVisibility(View.INVISIBLE);


    }
    //this will show(restore) everything in controller above

    public String getPathAndSetName(int adapterFinder,int position2){
        String filePath;
        switch (adapterFinder){
            case 1:
                filePath = String.valueOf(Constant.allMediaList.get(position2));
                textViewFileName.setText(Constant.allMediaList.get(position2).getName());
                break;
            case 2:
                filePath = String.valueOf(Constant.allHiddenMediaList.get(position2));
                textViewFileName.setText(Constant.allHiddenMediaList.get(position2).getName());
                break;
            case 3:
                filePath=String.valueOf(Constant.filteredMediaList.get(position2));
                textViewFileName.setText(Constant.filteredMediaList.get(position2).getName());
                break;
            case 4:
                filePath = String.valueOf(Constant.allVideoInFolder.get(position2));
                textViewFileName.setText(Constant.allVideoInFolder.get(position2).getName());
                break;
            case 7:
                filePath = String.valueOf(Constant.allVideoInLocker.get(position2));
                textViewFileName.setText(Constant.allVideoInLocker.get(position2).getName());
                break;
            default:
                if(position2<0 || position2>=Constant.allMediaList.size()){
                    position2=0;
                }
                filePath = String.valueOf(Constant.allMediaList.get(position2));
                textViewFileName.setText(Constant.allMediaList.get(position2).getName());
                break;
        }
        return filePath;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    assert data != null;
                    Uri uri = data.getData();
                    assert uri != null;
                    String path = uri.getPath();
                    assert path != null;
                    if(path.endsWith(".srt")) {
                        //buildSubtitleSource(uri);
                        simpleExoPlayer.seekTo(curPos);
                        imageViewCaption.setImageResource(R.drawable.ic_caption_black);
                    }
                    else{
                        Toast.makeText(this, "invalid subtitle file", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ignored) {
                    Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getSpecialFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //get duration when Next or Previous button is pressed
    public void getDuration(String path){
        try {
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(path);
            try {
                duration = Long.parseLong(
                        Objects.requireNonNull(metadataRetriever.extractMetadata(
                                MediaMetadataRetriever.METADATA_KEY_DURATION)));
            } catch (Exception ignored) {
                duration = 0;
            }
            Log.d("durationValue", String.valueOf(duration));
            forwardBackwardConstant = (float) Math.ceil((float) (duration + 1) / (2 * scrollSize));
            new Handler(Looper.getMainLooper()).postDelayed(makeViewVisible, 200);
            formattedDuration = "/" + formatTime(duration);
            isDurationTaken = true;
        }
        catch (Exception ignored){
            duration=0;
        }
    }
    //get duration when Next or Previous button is pressed
//
//    //hiding the status bar and navigation when creating activity first time
//    @SuppressWarnings("deprecation")
//    public void makeFullscreenAtFirst(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            //problem_may_rise not tested in android R emulator yet
//            final WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                insetsController.hide(WindowInsets.Type.statusBars());
//            }
//        }
//        else {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//    }
}

