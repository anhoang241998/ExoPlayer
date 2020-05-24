package com.example.exoplayerfullstack.exoplayer;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.exoplayerfullstack.glide.GlideThumbnailTransformation;
import com.github.rubensousa.previewseekbar.PreviewBar;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerManager implements PreviewLoader, PreviewBar.OnScrubListener {
    private ExoPlayerMediaSourceBuilder mediaSourceBuilder;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private ProgressBar mProgressBar;
    private PreviewTimeBar previewTimeBar;
    private String thumbnailsUrl;
    private ImageView imageView;
    private boolean resumeVideoOnPreviewStop;


    private Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY && playWhenReady) {
                previewTimeBar.hidePreview();
                mProgressBar.setVisibility(View.INVISIBLE);
            } else if (playbackState == Player.STATE_BUFFERING) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    };

    public ExoPlayerManager(PlayerView playerView, PreviewTimeBar previewTimeBar, ImageView imageView,
                            String thumbnailsUrl, ProgressBar progressBar) {
        this.playerView = playerView;
        this.imageView = imageView;
        this.mProgressBar = progressBar;
        this.previewTimeBar = previewTimeBar;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(playerView.getContext());
        this.resumeVideoOnPreviewStop = true;
        this.thumbnailsUrl = thumbnailsUrl;
        this.previewTimeBar.addOnScrubListener(this);
        this.previewTimeBar.setPreviewLoader(this);
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
        }
        player = createPlayer();
        playerView.setPlayer(player);
        playerView.setControllerShowTimeoutMs(15000);
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
                .override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
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
}
