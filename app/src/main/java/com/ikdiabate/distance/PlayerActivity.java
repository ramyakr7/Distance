package com.ikdiabate.distance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * @Author: Ibrahim Diabate
 * @Version: November 2017
 */

public class PlayerActivity extends YouTubeFailureRecoveryActivity {

    String name, desc, date, videoId, channel;
    public static final String EXTRA_TITLE = "videotitle";
    public static final String EXTRA_CHANNELTITLE = "channeltitle";
    public static final String EXTRA_DESC = "videodesc";
    public static final String EXTRA_DATE = "videodate";
    public static final String EXTRA_VIDEOID = "videoid";
    TextView title, channelTitle, videoDesc, videoPublishedDate;

    /**
     * Initialize objects used when the activity is launced
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        title = (TextView) findViewById(R.id.videoName);
        channelTitle = (TextView) findViewById(R.id.channelTitle);
        videoDesc = (TextView) findViewById(R.id.videoDescription);
        videoPublishedDate = (TextView) findViewById(R.id.datePublished);

       // Intent intentThatStartedThisActivity = getIntent();
        name = getIntent().getExtras().getString(EXTRA_TITLE);
        channel = getIntent().getExtras().getString(EXTRA_CHANNELTITLE);
        desc = getIntent().getExtras().getString(EXTRA_DESC);
        date = getIntent().getExtras().getString(EXTRA_DATE);
        videoId = getIntent().getExtras().getString(EXTRA_VIDEOID);

        setTitle(name);

        title.setText(name);
        channelTitle.setText(channel);
        videoDesc.setText(desc);
        videoPublishedDate.setText(videoId);


        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(BuildConfig.API_KEY_TOKEN, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(videoId);
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}
