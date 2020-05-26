package com.example.exoplayerfullstack;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exoplayerfullstack.exoplayer.ExoPlayerManager;
import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.github.vkay94.dtpv.DoubleTapPlayerView;
import com.github.vkay94.dtpv.SeekListener;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {


    @BindView(R.id.player_view)
    DoubleTapPlayerView mPlayerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private ExoPlayerManager exoPlayerManager;
    private PreviewTimeBar previewTimebar;
    private ScaleGestureDetector mGestureDetector;
    private float mScaleVideo = 1f;
    private SimpleExoPlayer player;
    private YouTubeOverlay mYouTubeOverlay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        previewTimebar = mPlayerView.findViewById(R.id.exo_progress);
        mYouTubeOverlay = mPlayerView.findViewById(R.id.youtubeDoubleTap);


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


        exoPlayerManager = new ExoPlayerManager(mPlayerView, previewTimebar, findViewById(R.id.imageView), getString(R.string.url_thumbnails), mProgressBar);
        exoPlayerManager.play(Uri.parse(getString(R.string.media_url_mp4)));



        player = ExoPlayerFactory.newSimpleInstance(this);
        mGestureDetector = new ScaleGestureDetector(this, new scaleListener());

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
        initializeDoubleTapPlayerView();
        mYouTubeOverlay.setPlayerView(mPlayerView);
        mYouTubeOverlay.setPlayer(player);
    }

    @Override
    public void onResume() {
        super.onResume();
        exoPlayerManager.onResume();
        mYouTubeOverlay.setPlayer(player);
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayerManager.onPause();
        mYouTubeOverlay.setPlayer(player);
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayerManager.onStop();
        mYouTubeOverlay.setPlayer(player);
    }

    private void requestFullScreenIfLandscape() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
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
                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            } else
                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    public void initializeDoubleTapPlayerView() {
        mYouTubeOverlay.setPlayerView(mPlayerView);
        mYouTubeOverlay.setAnimationDuration(800);
        mYouTubeOverlay.setFastForwardRewindDuration(10000);
        mYouTubeOverlay.setSeekListener(new SeekListener() {
            @Override
            public void onVideoStartReached() {
                pausePlayer();
                Log.d("VideoStart", "onVideoStartReached: " + "Video start reached");
            }

            @Override
            public void onVideoEndReached() {
                Log.d("VideoStop", "onVideoEndReached: " + "Video end reached");
            }
        });
        mYouTubeOverlay.setPerformListener(new YouTubeOverlay.PerformListener() {
            @Override
            public void onAnimationStart() {
                mPlayerView.setUseController(false);
                mYouTubeOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
                mPlayerView.setUseController(true);
                mYouTubeOverlay.setVisibility(View.GONE);
                if (!player.getPlayWhenReady())
                    mPlayerView.showController();
            }
        });

        mPlayerView.activateDoubleTap(true).setDoubleTapDelay(650).setDoubleTapListener(mYouTubeOverlay);
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }
}


