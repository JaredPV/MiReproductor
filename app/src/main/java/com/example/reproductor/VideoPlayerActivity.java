package com.example.reproductor;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    private String videoName, videoPath;
    private ImageButton ib_replay, ib_play, ib_forward;
    private SeekBar sb_video;
    private RelativeLayout controlsRL;
    boolean isOpen = true;
    private VideoView videoView;
    private RelativeLayout rl_controles, rl_video;
    private TextView tv_videoName, tv_videoTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoName = getIntent().getStringExtra("videoName");
        videoPath = getIntent().getStringExtra("videoPath");
        tv_videoName = findViewById(R.id.tv_videoTitle);
        tv_videoTime = findViewById(R.id.tv_tiempo);
        ib_replay = findViewById(R.id.ib_replay);
        ib_play = findViewById(R.id.ib_play);
        ib_forward = findViewById(R.id.ib_forward);
        sb_video = findViewById(R.id.sb_progreso);
        videoView = findViewById(R.id.VideoView);
        rl_controles = findViewById(R.id.rl_controles);
        rl_video = findViewById(R.id.rl_video);

        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                sb_video.setMax(videoView.getDuration());
                videoView.start();
            }
        });

        tv_videoName.setText(videoName);

        ib_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.seekTo(videoView.getDuration()-10000);
            }
        });
        ib_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoView.isPlaying()){
                    videoView.pause();
                    ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }else{
                    videoView.start();
                    ib_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                }
            }
        });
        ib_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.seekTo(videoView.getDuration()+10000);
            }
        });
        rl_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    hideControls();
                    isOpen = false;
                }else{
                    showControls();
                    isOpen = true;
                }
            }
        });

        setHandler();
        initializeSeekBar();
    }

    private void initializeSeekBar(){
        sb_video.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(sb_video.getId() == R.id.sb_progreso){
                    if(b){
                        videoView.seekTo(i);
                        videoView.start();
                        int curPos = videoView.getCurrentPosition();
                        tv_videoTime.setText(""+convertir(videoView.getDuration()-curPos));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setHandler(){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(videoView.getDuration()>0){
                    int cutPos = videoView.getCurrentPosition();
                    sb_video.setProgress(cutPos);
                    tv_videoTime.setText(""+convertir(videoView.getDuration()-cutPos));
                }
                handler.postDelayed(this,0);
            }
        };
        handler.postDelayed(runnable, 500);
    }
    private String convertir(int ms){
        String tiempo;
        int aux, segundos, minutos, horas;
        aux = ms/1000;
        segundos = aux%60;
        aux = aux/60;
        minutos = aux%60;
        aux = aux/60;
        horas = aux%24;
        if(horas!=0){
            tiempo = String.format("%02d",horas) + ":" + String.format("%02d",minutos) +":"+ String.format("%2d",segundos);
        }else {
            tiempo = String.format("%02d",minutos) + ":" + String.format("%02d",segundos);
        }
        return tiempo;
    }

    private void showControls() {
        rl_controles.setVisibility(View.VISIBLE);
        final Window window = this.getWindow();
        if(window==null){
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        View decorView = window.getDecorView();
        if(decorView!=null){
            int uiOption = decorView.getSystemUiVisibility();
            if(Build.VERSION.SDK_INT>=14){
                uiOption&=~View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if(Build.VERSION.SDK_INT>=16){
                uiOption&=~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if(Build.VERSION.SDK_INT>=19){
                uiOption&=~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOption);
        }
    }

    private void hideControls() {
        rl_controles.setVisibility(View.GONE);
        final Window window = this.getWindow();
        if(window==null){
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        View decorView = window.getDecorView();
        if(decorView!=null){
            int uiOption = decorView.getSystemUiVisibility();
            if(Build.VERSION.SDK_INT>=14){
                uiOption |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if(Build.VERSION.SDK_INT>=16){
                uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if(Build.VERSION.SDK_INT>=19){
                uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOption);
        }
    }
}