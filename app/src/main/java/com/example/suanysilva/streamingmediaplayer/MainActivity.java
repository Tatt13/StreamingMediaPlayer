package com.example.suanysilva.streamingmediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.example.suanysilva.streamingmediaplayer.api.Client;
import com.example.suanysilva.streamingmediaplayer.api.ServiceGenerator;
import com.example.suanysilva.streamingmediaplayer.api.models.Track;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private static final String TAG = "MainActivity";

    Button play, next, back;
    ProgressBar ProgressBar;
    CheckBox shuffle;
    List<Track> trackList;
    boolean tracksLoaded = false;
    /*String uo = "http://spankradio.cz/international/2017/songs/The%20Chainsmokers%20Ft.%20Coldplay%20-%20Something%20Just%20Like%20This%20(spankradio.cz).mp3";
    String url0 = "https://cldup.com/-TIo9vr6mt.mp3?download=Christina%20Perri%20-%20A%20Thousand%20Years%20_Ugblizz%20Music.mp3";
    String url2 = "http://www.similarsong.com/sites/similarsong.com/files/song/7790/adele-hello.mp3";
    String url3 = "http://www.hitsmp3.com.br/download/ed-sheeran-perfect-sympony-feat-andrea-bocelli-cover?id=http://api.soundcloud.com/tracks/370116647/stream?client_id=13621aa6735d37f4da3828200e323844&.mp3";
    String url4 = "http://46.165.246.205/dl/s1/Lb28hgbyEqS9wzoKOI9vkg,1516641007/yt:JRVD-pndqY0-1/Ed%20Sheeran%20-%20Photograph%20%28tradu%C3%A7%C3%A3o%29%20-%20trilha%20sonora%20de%20Como%20eu%20era%20antes%20de%20voc%C3%AA.mp3";
    String url5 = "http://78.47.157.189/dl/s11/R5I52zIj5C0AZQflNE4EoA,1516641207/yt:fanSSzNGOcU-1/Tequila%20pala%20Raz%C3%B3n-%20Pedro%20Cap%C3%B3%2C%20Christian%20Pag%C3%A1n%2C%20Luis%20Figueroa.mp3";
    String url6 = "http://78.47.240.70/dl/s7/GfYKUg3MDz3bA-P2O23SvQ,1516713960/yt:8j9zMok6two-1/Miley%20Cyrus%20-%20Malibu%20%28Official%20Video%29.mp3";
    String url7 = "http://78.47.240.70/dl/s1/VlmAnZcScWwqi1l91UEpag,1516641348/yt:J_ub7Etch2U-1/Sam%20Smith%20-%20Too%20Good%20At%20Goodbyes%20%28Official%20Video%29.mp3";
    String url8 = "http://78.47.157.189/dl/s2/neXasH3THF73nZwMr7ax0A,1516641445/yt:qt_OkgSOrkU-1/Andrea%20Bocelli%2C%20Celine%20Dion%20-%20The%20Prayer.mp3";
    String url9 = "http://94.130.135.151/dl/s6/3xKdGKaCZQkwaicGAwgEJA,1516641611/yt:lp-EO5I60KA-1/Ed%20Sheeran%20-%20Thinking%20Out%20Loud%20%5BOfficial%20Video%5D.mp3";


    String musicas [] = {uo, url0, url2, url3, url4, url5, url6, url7, url8, url9};*/
    int position = 0;
    boolean isPlaying = false;
    int overflow;
    boolean shuffleOn;
    public Runnable mProgressUpdate = new Runnable() {
        @Override
        public void run() {
            float position = mediaPlayer.getCurrentPosition();
            ProgressBar.setProgress((int) position);
            overflow--;
            int delay =(1500 - ((int) position % 1000));
            if(overflow<0){
                overflow++;
                ProgressBar.postDelayed(mProgressUpdate, delay);
            }


        }
    };

    View.OnClickListener nextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mediaPlayer.reset();
            prepareStream(++position, trackList);
        }
    };

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mediaPlayer.reset();
            prepareStream(--position, trackList);
        }
    };

    View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPlaying){
                play.setText("Play");
                mediaPlayer.pause();
                isPlaying = false;
            }else if(tracksLoaded && isPlaying) {
                play.setText("Pause");
                prepareStream(position, trackList);
                isPlaying = true;
            }
        }
    };

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.play);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        shuffle = findViewById(R.id.shuffle);
        ProgressBar = findViewById(R.id.progressBar);
        next.setOnClickListener(nextListener);
        play.setOnClickListener(playListener);
        back.setOnClickListener(backListener);
        ProgressBar.setProgress(0);

        shuffle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    shuffleOn = true;
                }else{
                    shuffleOn = false;
                }

            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                prepareStream(++position, trackList);
                Log.d(TAG, "onCompletion: completo!");
                mediaPlayer.reset();
                if (shuffleOn) {
                    int randomTrack = (int) (trackList.size() * Math.random());
                    Log.d(TAG, "onCompletion: randomTrack" + randomTrack);
                    prepareStream(randomTrack,trackList);
                } else {
                    prepareStream(++position, trackList);
                }
            }
        });

        getTracks();
    }

    private void getTracks() {
        Client.Tracks client = ServiceGenerator.createService(Client.Tracks.class);
        client.getTracks().enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if(response.code() == 200){
                    trackList = response.body();
                    Log.d(TAG, "onResponse: " + response.body().size());
                }else{
                    Log.d(TAG, "onResponse: code ==" + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.e(TAG, "onFailure: Ocorreu um erro!");
            }
        });
    }

    private void prepareStream(int position, List<Track> tracksList) {
        try {
            if (position > -1 && position < trackList.size()){
                mediaPlayer.setDataSource(trackList.get(position).getTrackUrl());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
            }else{
                this.position = 0;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: chamado com sucesso!");
        mediaPlayer.start();
        isPlaying = true;
        ProgressBar.setMax(mediaPlayer.getDuration());
        ProgressBar.postDelayed(mProgressUpdate, 10);
        Log.d(TAG, "onPrepared: " + mediaPlayer.getDuration());
    }
}
