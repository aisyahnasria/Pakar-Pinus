package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class PineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinus);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.pine_tlbr);
        setSupportActionBar(toolbar);
        YouTubePlayerView youtubePlayerview = findViewById(R.id.videoView);
        getLifecycle().addObserver(youtubePlayerview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        youtubePlayerview.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady( YouTubePlayer youTubePlayer) {
                String videoId = "RXzytrnn3Yc";  // Ganti dengan ID video YouTube
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YouTubePlayerView youtubePlayerView = findViewById(R.id.videoView);
        youtubePlayerView.release();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


}

