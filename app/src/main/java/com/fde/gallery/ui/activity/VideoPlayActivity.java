/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fde.gallery.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.fde.gallery.R;
import com.fde.gallery.base.BaseActivity;
import com.fde.gallery.bean.Video;
import com.fde.gallery.utils.LogTools;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class VideoPlayActivity extends BaseActivity {
    private SimpleExoPlayer mSimpleExoPlayer;
    private StyledPlayerView mStyledPlayerView;
    private DefaultTrackSelector mDefaultTrackSelector;
    private DefaultTrackSelector.Parameters mDefaultTrackSelectorParameters;
    private Video videoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        mStyledPlayerView = findViewById(R.id.player_view);
        videoData = (Video) getIntent().getSerializableExtra("video_data");
        initPlayer();
    }

    /**
     * init video play
     */
    private void initPlayer(){
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        mStyledPlayerView.setPlayer(player);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "openfde")); // replace 'yourAppName' with your app's name
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoData.getPath()));
        player.prepare(videoSource);
        player.setPlayWhenReady(false);
    }


    /**
     * release player
     */
    private void releasePlayer(){
        if (mSimpleExoPlayer == null) return;
        mSimpleExoPlayer.release();
        mSimpleExoPlayer = null;
        mDefaultTrackSelector = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayer();
        mStyledPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStyledPlayerView.onPause();
        releasePlayer();
    }
}