package com.example.exoplayerfullstack;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.exoplayerfullstack.exoplayer.ExoPlayerManager;
import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.github.vkay94.dtpv.DoubleTapPlayerView;
import com.github.vkay94.dtpv.SeekListener;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {


    @BindView(R.id.player_view)
    DoubleTapPlayerView mPlayerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.youtubeDoubleTap)
    YouTubeOverlay mYouTubeOverlay;

    private ExoPlayerManager exoPlayerManager;
    private PreviewTimeBar previewTimebar;
    private Button mBtnFullScreen, mBtnLockScreen;
    private ImageButton mBtnUnlockScreen;
    private RelativeLayout mUnlockRelative;
    private ConstraintLayout mViewController;
    private FrameLayout mViewGroupPlayPause;
    private boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        previewTimebar = mPlayerView.findViewById(R.id.exo_progress);

        mBtnFullScreen = mPlayerView.findViewById(R.id.btn_fullScreen);

        mBtnLockScreen = mPlayerView.findViewById(R.id.btn_lock_screen);
        mBtnUnlockScreen = mPlayerView.findViewById(R.id.btn_unlock);
        mUnlockRelative = mPlayerView.findViewById(R.id.viewGroup_unlock_screen);
        mViewController = mPlayerView.findViewById(R.id.viewGroup_controller);
        mViewGroupPlayPause = mPlayerView.findViewById(R.id.viewGroup_play_pause);


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


        exoPlayerManager = new ExoPlayerManager(mPlayerView, previewTimebar, findViewById(R.id.imageView), getString(R.string.url_thumbnails), mProgressBar, mYouTubeOverlay, mViewGroupPlayPause);
        exoPlayerManager.play(Uri.parse(getString(R.string.media_url_mp4)));

        exoPlayerManager.initializeDoubleTapPlayerView();
        requestFullScreenIfLandscape();

        mBtnFullScreen.setOnClickListener(v -> {
            if (flag) {
                mBtnFullScreen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fullscreen, 0, 0, 0);
                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                flag = false;
            } else {
                mBtnFullScreen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fullscreen_exit, 0, 0, 0);
                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                flag = true;
            }
        });

        mBtnLockScreen.setOnClickListener(v -> {
           mViewController.setVisibility(View.GONE);
           mUnlockRelative.setVisibility(View.VISIBLE);
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        });

       mBtnUnlockScreen.setOnClickListener(v -> {
           mUnlockRelative.setVisibility(View.GONE);
           mViewController.setVisibility(View.VISIBLE);
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
       });

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
        mYouTubeOverlay.setPlayer(exoPlayerManager.getPlayer());
    }

    @Override
    public void onResume() {
        super.onResume();
        exoPlayerManager.onResume();
        mYouTubeOverlay.setPlayer(exoPlayerManager.getPlayer());
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
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}


