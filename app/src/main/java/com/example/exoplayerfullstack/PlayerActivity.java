package com.example.exoplayerfullstack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exoplayerfullstack.exoplayer.ExoPlayerManager;
import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity{

    @BindView(R.id.player_view)
    SimpleExoPlayerView playerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBarVideo;
    private ExoPlayerManager exoPlayerManager;
    private PreviewTimeBar previewTimebar;
    private ScaleGestureDetector mGestureDetector;
    private float mScaleVideo = 1f;
    private SimpleExoPlayer player;
    private DisplayMetrics mDisplayMetrics;
    private int mScreenWidth;


    @SuppressLint("ClickableViewAccessibility")
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

        player = ExoPlayerFactory.newSimpleInstance(this);
        mGestureDetector = new ScaleGestureDetector(this, new scaleListener());

        requestFullScreenIfLandscape();

        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mScreenWidth = mDisplayMetrics.widthPixels;
        Log.d("TAG1",  "width " + mScreenWidth );

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



    private class scaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleVideo = mScaleVideo * detector.getScaleFactor();
            if (mScaleVideo > 1f) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            } else
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }
}


