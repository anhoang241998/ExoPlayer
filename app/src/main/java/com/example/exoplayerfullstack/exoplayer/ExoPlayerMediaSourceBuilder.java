package com.example.exoplayerfullstack.exoplayer;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayerMediaSourceBuilder {
    private DefaultBandwidthMeter mBandwidthMeter;
    private Context mContext;
    private Uri mUri;
    private int streamType;

    public ExoPlayerMediaSourceBuilder(Context context) {
        this.mBandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
        this.mContext = context;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
        this.streamType = Util.inferContentType(uri.getLastPathSegment());
    }

    public MediaSource getMediaSource(boolean preview) {
        switch (streamType) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultDataSourceFactory(mContext, null,
                        getHttpDataSourceFactory(preview))).createMediaSource(mUri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDataSourceFactory(mContext, null,
                        getHttpDataSourceFactory(preview))).createMediaSource(mUri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(getDataSourceFactory(preview)).createMediaSource(mUri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(getDataSourceFactory(preview)).createMediaSource(mUri);
            default: {
                throw new IllegalStateException("Unsupported type: " + streamType);
            }
        }
    }
    private DataSource.Factory getDataSourceFactory(boolean preview) {
        return new DefaultDataSourceFactory(mContext, preview ? null : mBandwidthMeter,
                getHttpDataSourceFactory(preview));
    }

    private DataSource.Factory getHttpDataSourceFactory(boolean preview) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(mContext,
                "ExoPlayerDemo"), preview ? null : mBandwidthMeter);
    }
}
