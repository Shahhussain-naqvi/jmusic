package com.example.jmusic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Playsong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    ImageView back,play,next,volume_down;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String text_content;
    int position;
    SeekBar seekbar;
    Thread updateSeek;
    SeekBar volumebar;
    AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);

        //VIEWS_BY_ID

        textView = findViewById(R.id.current_song);
        back = findViewById(R.id.back);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        volume_down = findViewById(R.id.volume_down);
        seekbar = findViewById(R.id.seekbar);
        volumebar = findViewById(R.id.volumebar);

        //INTENT

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs= (ArrayList) bundle.getParcelableArrayList("songslist");
        text_content = intent.getStringExtra("currentSong");
        textView.setText(text_content);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();

        // FOR VOLUME BAR

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumebar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumebar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)== audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)){
                    volume_down.setImageResource(R.drawable.volume_mute);
                }
                else{
                    volume_down.setImageResource(R.drawable.volume_down);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // FOR SEEKBAR

        seekbar.setMax(mediaPlayer.getDuration());
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentposition =0 ;
                try {
                    while (currentposition < mediaPlayer.getDuration()){
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentposition);
                        sleep(800);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        //SETTING CLICK LISTENER ON PLAY BUTTON

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }

        });

        //SETTING CLICK LISTENER ON BACK BUTTON

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekbar.setProgress(0);
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position = position - 1;
                }
                else{
                    position=songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekbar.setMax(mediaPlayer.getDuration());
                text_content=songs.get(position).getName().toString();
                textView.setText(text_content);
            }

        });

        //SETTING CLICK LISTENER ON NEXT BUTTON

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                seekbar.setProgress(0);
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position +1 ;
                }
                else{
                    position=0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekbar.setMax(mediaPlayer.getDuration());
                text_content=songs.get(position).getName().toString();
                textView.setText(text_content);
            }

        });



    }
}