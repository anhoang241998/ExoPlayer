package com.example.exoplayerfullstack.exoplayer;

import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.ProgressBar;

import com.example.exoplayerfullstack.PlayerActivity;
import com.example.exoplayerfullstack.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerManager {
    private ExoPlayerMediaSourceBuilder mediaSourceBuilder;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private ProgressBar mProgressBar;
    private boolean resumeVideoOnPreviewStop;

    private Player.EventListener eventListener = new Player.EventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY && playWhenReady){
                mProgressBar.setVisibility(View.INVISIBLE);
            }else if (playbackState == Player.STATE_BUFFERING){
                mProgressBar.setVisibility(View.VISIBLE);
            }else{
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    };

    public ExoPlayerManager(PlayerView playerView, ProgressBar progressBar) {
        this.playerView = playerView;
        this.mProgressBar = progressBar;
        this.mediaSourceBuilder = new ExoPlayerMediaSourceBuilder(playerView.getContext());
        this.resumeVideoOnPreviewStop = true;
    }

    public void  play(Uri uri){
        mediaSourceBuilder.setUri(uri);
    }

    public void OnStart(){
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
}
