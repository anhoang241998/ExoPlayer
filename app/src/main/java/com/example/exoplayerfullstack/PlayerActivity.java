package com.example.exoplayerfullstack;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exoplayerfullstack.exoplayer.ExoPlayerManager;
import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

    @BindView(R.id.player_view)
    SimpleExoPlayerView playerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBarVideo;
    private ExoPlayerManager exoPlayerManager;
    private PreviewTimeBar previewTimebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        previewTimebar = playerView.findViewById(R.id.exo_progress);

        previewTimebar.addOnPreviewVisibilityListener((previewBar, isPreviewShowing) -> {
            Log.d("PreviewShowing", String.valueOf(isPreviewShowing));
        });

        previewTimebar.addOnScrubListener(new PreviewBar.OnScrubListener() {
            @Override
            public void onScrubStart(PreviewBar previewBar) {
                Log.d("Scrub", "START");
            }

            @Override
            public void onScrubMove(PreviewBar previewBar, int progress, boolean fromUser) {
                Log.d("Scrub", "MOVE to " + progress / 1000 + " FROM USER: " + fromUser);
            }

            @Override
            public void onScrubStop(PreviewBar previewBar) {
                Log.d("Scrub", "STOP");
            }
        });


        exoPlayerManager = new ExoPlayerManager(playerView, previewTimebar, findViewById(R.id.imageView), getString(R.string.url_thumbnails), mProgressBarVideo);
        exoPlayerManager.play(Uri.parse(getString(R.string.media_url_mp4)));

        requestFullScreenIfLandscape();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            requestFullScreenIfLandscape();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        exoPlayerManager.OnStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayerManager.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayerManager.onStop();
    }

    private void requestFullScreenIfLandscape() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}

