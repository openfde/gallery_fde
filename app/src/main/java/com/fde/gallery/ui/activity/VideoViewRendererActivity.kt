package com.fde.gallery.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import com.bella.dlna.BaseRendererActivity
import com.bella.dlna.RenderControl
import com.bella.dlna.RenderState
import com.fde.gallery.R
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoViewRendererActivity : BaseRendererActivity() {

    private val playView: StyledPlayerView by lazy { findViewById(R.id.player_view) }
    private var renderState: RenderState = RenderState.IDLE
        set(value) {
            if (field != value) {
                field = value
                rendererService?.notifyAvTransportLastChange(field)
            }
        }

    override fun onServiceConnected() {
//        rendererService?.bindRealPlayer(VideoViewRenderControl(videoView))
        openMedia()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoview_renderer)
        initComponent()
        rendererService?.run {
            openMedia()
        }
    }

    private fun initPlayer( videoData: Uri) {
        val rendererFactory = DefaultRenderersFactory(this, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        val trackSelector = DefaultTrackSelector()
        val builder = DefaultLoadControl.Builder()
        builder.setAllocator(DefaultAllocator(true, 2 * 1024 * 1024))
        builder.setBufferDurationsMs(2000, 5000, 1500, 0)
        val loadControl = builder.createDefaultLoadControl()


        playView.controllerAutoShow = true
        playView.controllerShowTimeoutMs = 3000
        playView.setShowNextButton(false)
        playView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_NEVER)
        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
        playView.player = player
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "openfde"))
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(videoData)
        player.prepare(videoSource)
        player.playWhenReady = true

    }


    @SuppressLint("SetTextI18n")
    private fun initComponent() {

    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        openMedia()
    }

    private var nextURI: String? = null

    @SuppressLint("SetTextI18n")
    private fun openMedia() {
        castAction?.currentURI?.run {
            initPlayer(Uri.parse(this))
        }
        castAction?.nextURI?.run {
            nextURI = this
        }
        castAction?.stop?.run {
            finish()
        }
    }

    override fun onDestroy() {
        renderState = RenderState.STOPPED
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val handled = super.onKeyDown(keyCode, event)
        if (rendererService != null) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
                val volume = (application.getSystemService(AUDIO_SERVICE) as AudioManager).getStreamVolume(AudioManager.STREAM_MUSIC)
                rendererService?.notifyRenderControlLastChange(volume)
            } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

            }
        }
        return handled
    }

    private inner class VideoViewRenderControl(private val videoView: VideoView) : RenderControl {
        override val currentPosition: Long
            get() = videoView.currentPosition.toLong()
        override val duration: Long
            get() = videoView.duration.toLong()

        override fun play(speed: Double?) { // video view 不支持倍速播放
            videoView.start()
            renderState = RenderState.PLAYING
        }

        override fun pause() {
            videoView.pause()
            renderState = RenderState.PAUSED
        }

        override fun seek(millSeconds: Long) = videoView.seekTo(millSeconds.toInt())
        override fun stop() {
            videoView.stopPlayback()
            renderState = RenderState.STOPPED
            // close player
            finish()
        }

        override fun getState(): RenderState = renderState
    }
}

