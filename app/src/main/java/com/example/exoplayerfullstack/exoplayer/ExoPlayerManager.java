package com.example.exoplayerfullstack.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.exoplayerfullstack.glide.GlideThumbnailTransformation;
import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.github.vkay94.dtpv.DoubleTapPlayerView;
import com.github.vkay94.dtpv.SeekListener;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerManager implements PreviewLoader, PreviewBar.OnScrubListener {
    private static Context sContext;
    private ExoPlayerMediaSourceBuilder mediaSourceBuilder;
    private DoubleTapPlayerView playerView;
    private SimpleExoPlayer player;
    private ProgressBar mProgressBar;
    private PreviewTimeBar previewTimeBar;
    private String thumbnailsUrl;
    private ImageView imageView;
    private YouTubeOverlay mYouTubeOverlay;
    private boolean resumeVideoOnPreviewStop;
    private FrameLayout viewGroupPlayPause;


    public SimpleExoPlayer getPlayer() {
        return player;
    }

    private Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY && playWhenReady) {
                previewTimeBar.hidePreview();
                mProgressBar.setVisibility(View.INVISIBLE);
                viewGroupPlayPause.setVisibility(View.VISIBLE);
            } else if (playbackState == Player.STATE_BUFFERING) {
                mProgressBar.setVisibility(View.VISIBLE);
                viewGroupPlayPause.setVisibility(View.INVISIBLE);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                viewGroupPlayPause.setVisibility(View.VISIBLE);
            }
        }
    };

    public ExoPlayerManager(DoubleTapPlayerView playerView, PreviewTimeBar previewTimeBar, ImageView imageView,
                            String thumbnailsUrl, ProgressBar progressBar, YouTubeOverlay youTubeOverlay, FrameLayout viewGroupPlayPause) {
        this.playerView = playerView;
        this.imageView = imageView;
        this.mProgressBar = progressBar;
        this.previewTimeBar = previewTimeBar;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(playerView.getContext());
        this.resumeVideoOnPreviewStop = true;
        this.thumbnailsUrl = thumbnailsUrl;
        this.previewTimeBar.addOnScrubListener(this);
        this.previewTimeBar.setPreviewLoader(this);
        this.mYouTubeOverlay = youTubeOverlay;
        this.viewGroupPlayPause = viewGroupPlayPause;

    }

    public void play(Uri uri) {
        mediaSourceBuilder.setUri(uri);
    }

    public void OnStart() {
        if (Util.SDK_INT > 23) {
            createPlayers();
        }
    }

    public void onResume() {
        if (Util.SDK_INT <= 23) {
            createPlayers();
        }
    }

    public void onPause() {
        if (Util.SDK_INT <= 23) {
            releasePlayers();
        }
    }

    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayers();
        }
    }

    public void setResumeVideoOnPreviewStop(boolean resume) {
        this.resumeVideoOnPreviewStop = resume;
    }

    private void releasePlayers() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void createPlayers() {
        if (player != null) {
            player.release();
        }else {
            player = createPlayer();
            playerView.setPlayer(player);

        }
    }

    private SimpleExoPlayer createPlayer() {
            SimpleExoPlayer player = new SimpleExoPlayer.Builder(playerView.getContext()).build();
            player.setPlayWhenReady(true);
            player.prepare(mediaSourceBuilder.getMediaSource(false));
            player.addListener(eventListener);
        return player;
    }



    @Override
    public void loadPreview(long currentPosition, long max) {
        if (player.isPlaying()) {
            player.setPlayWhenReady(false);
        }
        Glide.with(imageView)
                .load(thumbnailsUrl)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transform(new GlideThumbnailTransformation(currentPosition))
                .into(imageView);
    }

    @Override
    public void onScrubStart(PreviewBar previewBar) {
        player.setPlayWhenReady(false);
    }

    @Override
    public void onScrubMove(PreviewBar previewBar, int progress, boolean fromUser) {

    }

    @Override
    public void onScrubStop(PreviewBar previewBar) {
        if (resumeVideoOnPreviewStop) {
            player.setPlayWhenReady(true);
        }
    }

    public void initializeDoubleTapPlayerView() {
        mYouTubeOverlay.setPlayerView(playerView);
        mYouTubeOverlay.setAnimationDuration(800);
        mYouTubeOverlay.setFastForwardRewindDuration(10000);
        mYouTubeOverlay.setSeekListener(new SeekListener() {
            @Override
            public void onVideoStartReached() {
                pausePlayer();
            }

            @Override
            public void onVideoEndReached() {
            }
        });
        mYouTubeOverlay.setPerformListener(new YouTubeOverlay.PerformListener() {
            @Override
            public void onAnimationStart() {
                playerView.setUseController(false);
                mYouTubeOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
                playerView.setUseController(true);
                mYouTubeOverlay.setVisibility(View.GONE);
                if (!player.getPlayWhenReady())
                    playerView.showController();
            }
        });

        playerView.activateDoubleTap(true).setDoubleTapDelay(650).setDoubleTapListener(mYouTubeOverlay);
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }


}




