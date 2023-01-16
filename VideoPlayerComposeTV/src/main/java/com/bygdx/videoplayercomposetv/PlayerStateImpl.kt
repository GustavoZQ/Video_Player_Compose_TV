package com.bygdx.videoplayercomposetv

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bygdx.videoplayercomposetv.interfaces.PlayerControls
import com.bygdx.videoplayercomposetv.interfaces.PlayerState
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerStateImpl(
    override val exoPlayer: ExoPlayer,
    private val coroutineScope: CoroutineScope,
    private val hideControlsAfter: Long = 5000L,
) : PlayerState, Player.Listener {
    override val title: MutableState<String> = mutableStateOf("")
    override var videoPosition: MutableState<Long> = mutableStateOf(0L)
    override var videoDuration: MutableState<Long> = mutableStateOf(0L)
    override val bufferPosition: MutableState<Long> = mutableStateOf(0L)
    override val isBuffering: MutableState<Boolean> = mutableStateOf(true)
    override var isPlaying: MutableState<Boolean> = mutableStateOf(false)
    override var state: MutableState<Int> = mutableStateOf(0)
    override var isControlUiVisible: MutableState<Boolean> = mutableStateOf(false)
    private var lastInteraction = 0L
    override val controls: PlayerControls
        get() = object : PlayerControls {
            override fun play() {
                exoPlayer.play()
                lastInteraction = 0
            }

            override fun pause() {
                exoPlayer.pause()
                lastInteraction = 0
            }

            override fun forward() {
                exoPlayer.seekForward()
                lastInteraction = 0
            }

            override fun rewind() {
                exoPlayer.seekBack()
                lastInteraction = 0
            }
        }
    override val items: MutableState<List<ExoItem>> = mutableStateOf(listOf())


    override fun onIsPlayingChanged(isPlaying1: Boolean) {
        isPlaying.value = isPlaying1
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        state.value = playbackState
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                isBuffering.value = true
            }
            Player.STATE_READY -> {
                videoDuration.value = exoPlayer.duration
                isBuffering.value = false
            }
            else -> {}
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        Log.i("TAG", "onMediaItemTransition: $mediaItem.")
    }

    override fun onPlayerError(error: PlaybackException) {

    }

    override fun hideControls() {
        isControlUiVisible.value = false
    }

    override fun showControls() {
        lastInteraction = 0
        isControlUiVisible.value = true
        coroutineScope.launch {
            while (true) {
                videoPosition.value = exoPlayer.currentPosition
                bufferPosition.value = exoPlayer.bufferedPosition
                if (items.value.isNotEmpty()) {
                    title.value = items.value[exoPlayer.currentMediaItemIndex].title
                }
                delay(500)
                lastInteraction += 500
                if (lastInteraction >= hideControlsAfter) {
                    hideControls()
                    break
                }
            }
        }
    }

    fun buildMediaSourceFromItems(referer: String? = null, link: String): MediaSource {
        val factory = DefaultHttpDataSource.Factory()

        val dataSourceFactory =
            if (referer?.contains("uqload") == true)
                factory.setDefaultRequestProperties(
                    hashMapOf("Referer" to referer)
                )
            else factory

        return when (Util.inferContentType(Uri.parse(link))) {
            C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(link)))
            C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(link)))
            else -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(link)))
        }
    }

    fun buildMediaSource(referer: String? = null, link: String) {
        val factory = DefaultHttpDataSource.Factory()

        val dataSourceFactory =
            if (referer?.contains("uqload") == true)
                factory.setDefaultRequestProperties(hashMapOf("Referer" to referer))
            else factory

        when (Util.inferContentType(Uri.parse(link))) {
            C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(link)))
            C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(link)))
            else -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(link)))
        }.let {
            exoPlayer.apply {
                setMediaSource(it)
                exoPlayer.prepare()
                playWhenReady = true
            }
        }
    }

    override fun togglePlayPause() {
        if (isPlaying.value)
            controls.pause()
        else
            controls.play()
    }

    override fun visibleControlsRemain() {
        lastInteraction = 0
    }

    override fun movePosition(toLong: Long) {
        exoPlayer.seekTo(toLong)
    }

    override fun setItems(items: List<ExoItem>) {
        this.items.value = items
        val mediaSources = mutableListOf<MediaSource>()
        items.forEach {
            mediaSources.add(buildMediaSourceFromItems(link = it.link))
        }
        exoPlayer.setMediaSources(mediaSources)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun getCurrentTitle(): String = title.value

}

