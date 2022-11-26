package com.privatebrowser.safebrowser.download.video.videos.player;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;


public class PlayerManager {

    GlobalVar mGlobalVar = GlobalVar.getInstance();

    private  ExoPlayer simpleExoPlayer;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    Context context;

    public PlayerManager(Context context) {
        this.context = context;
        initExoPlayer();
    }

    public ExoPlayer getCurrentPlayer(){
        return simpleExoPlayer;
    }
    public ExoPlayer getSimpleExoPlayer(){
        return simpleExoPlayer;
    }
    private void initExoPlayer(){
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name)));
//        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
//
//        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//        simpleExoPlayer =  ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        trackSelector = new DefaultTrackSelector(context);
        trackSelector.setParameters(trackSelector.buildUponParameters().setAllowVideoMixedMimeTypeAdaptiveness(true));
        simpleExoPlayer =  new ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .build();

        simpleExoPlayer.setPlayWhenReady(true);

    }

    public void prepare(boolean resetPosition, boolean resetState){
        MediaItem mediaItem = MediaItem.fromUri((Uri.parse(GlobalVar.getInstance().getPath())));
        simpleExoPlayer.setMediaItem(mediaItem,resetPosition);
        simpleExoPlayer.prepare();
    }
    public boolean getPlayWhenReady(){
        if(simpleExoPlayer == null)
            return false;
        return simpleExoPlayer.getPlayWhenReady();
    }
    public void setPlayWhenReady(boolean value){
        if(simpleExoPlayer == null) return;
        simpleExoPlayer.setPlayWhenReady(value);
    }
    @SuppressLint("SuspiciousIndentation")
    public void setVolume(float volume){
        if(simpleExoPlayer == null ) return;
            simpleExoPlayer.setVolume(volume);
    }
    public void onFullScreen() {
        if (simpleExoPlayer == null) return;
        mGlobalVar.seekPosition = simpleExoPlayer.getCurrentPosition();
        mGlobalVar.isPlaying = simpleExoPlayer.getPlayWhenReady();

    }
    public void releasePlayer(){
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            trackSelector = null;
        }
    }


//    private static MediaQueueItem buildMediaQueueItem(VideoItem videoItem) {
//        String type = MimeTypes.BASE_TYPE_VIDEO + "/" + kxUtils.getFileExtension(videoItem.getPath());
//        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_TV_SHOW);
//        movieMetadata.putString(MediaMetadata.KEY_TITLE, videoItem.getVideoTitle());
//        MediaInfo mediaInfo = new MediaInfo.Builder(videoItem.getPath())
//                .setStreamType(MediaInfo.STREAM_TYPE_NONE).setContentType(type)
//                .setMetadata(movieMetadata).build();
//        return new MediaQueueItem.Builder(mediaInfo).build();
//    }

}
