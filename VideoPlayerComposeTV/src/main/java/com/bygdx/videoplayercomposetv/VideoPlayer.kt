package com.bygdx.videoplayercomposetv

import android.view.KeyEvent
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.bygdx.videoplayercomposetv.interfaces.PlayerState
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun VideoPlayer(
    playerState: PlayerState,
    navController: NavController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    videoPlayerControls: @Composable () -> Unit,
) {
    var timeAction by remember { mutableStateOf(0L) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onKeyEvent {
                if ((it.nativeKeyEvent.eventTime - timeAction) >= 200L) {
                    playerState.apply {
                        when (it.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_BACK -> {
                                timeAction = it.nativeKeyEvent.eventTime
                                navController.navigateUp()
                            }
                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                timeAction = it.nativeKeyEvent.eventTime
                                if (!isControlUiVisible.value) {
                                    controls.forward()
                                }
                            }
                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                timeAction = it.nativeKeyEvent.eventTime
                                if (!isControlUiVisible.value) {
                                    controls.rewind()
                                }
                            }
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                if (!isControlUiVisible.value) {
                                    showControls()
                                }
                            }
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                if (!isControlUiVisible.value) {
                                    showControls()
                                }
                            }
                            else -> {}
                        }
                    }
                }
                true
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!playerState.isControlUiVisible.value)
                    playerState.showControls()
            }
    ) {
        PlayerView(
            playerState = playerState
        )
        ControlsVisibility(
            visibility = playerState.isControlUiVisible.value
        ) {
            videoPlayerControls()
        }
        LoadingIndicator(
            buffering = playerState.isBuffering.value,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> playerState.showControls()
                Lifecycle.Event.ON_STOP -> playerState.exoPlayer.pause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerState.exoPlayer.removeListener(playerState)
            playerState.exoPlayer.release()
        }
    }
}

@Composable
fun PlayerView(
    playerState: PlayerState,
) {
    AndroidView(
        factory = {
            StyledPlayerView(it).apply {
                player = playerState.exoPlayer
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                useController = false
            }
        }
    )
}

@Composable
fun ControlsVisibility(
    visibility: Boolean,
    controls: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visibility,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        controls()
    }
}

@Composable
fun LoadingIndicator(
    buffering: Boolean = false,
    modifier: Modifier,
) {
    if (buffering) {
        CircularProgressIndicator(
            color = Color.Red,
            strokeWidth = 7.dp,
            modifier = modifier
                .requiredSize(70.dp)
        )
    }
}
