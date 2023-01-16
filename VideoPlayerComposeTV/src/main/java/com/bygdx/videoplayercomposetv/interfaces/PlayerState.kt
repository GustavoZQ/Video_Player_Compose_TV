package com.bygdx.videoplayercomposetv.interfaces

import androidx.compose.runtime.MutableState
import com.bygdx.videoplayercomposetv.ExoItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

interface PlayerState : Player.Listener {
    val exoPlayer: ExoPlayer
    val title: MutableState<String>
    val isBuffering: MutableState<Boolean>
    val bufferPosition: MutableState<Long>
    var videoPosition: MutableState<Long>
    var videoDuration: MutableState<Long>
    var isPlaying: MutableState<Boolean>
    var state: MutableState<Int>
    var isControlUiVisible: MutableState<Boolean>
    val controls: PlayerControls
    val items: MutableState<List<ExoItem>>
    fun hideControls()
    fun showControls()
    fun togglePlayPause()
    fun visibleControlsRemain()
    fun movePosition(toLong: Long)
    fun setItems(items: List<ExoItem>)
    fun getCurrentTitle(): String
}