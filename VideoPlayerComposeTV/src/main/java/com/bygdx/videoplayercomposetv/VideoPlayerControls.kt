package com.bygdx.videoplayercomposetv

import android.view.KeyEvent.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animextv.bygdx.presentation.player.utils.Pause
import com.animextv.bygdx.presentation.player.utils.PlayArrow
import com.animextv.bygdx.presentation.player.utils.SkipNext
import com.animextv.bygdx.presentation.player.utils.SkipPrevious
import com.bygdx.videoplayercomposetv.interfaces.PlayerState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoPlayerControls(
    playerState: PlayerState,
    title: String,
    background: Color = Color.Black.copy(alpha = 0.35f),
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(background)
        .padding(16.dp)
    ) {
        val (timeLine, skipPrevious, playPause, skipNext) = remember { FocusRequester.createRefs() }

        Header(
            title = title,
            modifier = Modifier.weight(1f)
        )
        CenterControls(
            playerState = playerState,
            modifier = Modifier.weight(1f),
            requesters = listOf(timeLine, skipPrevious, playPause, skipNext)
        )
        TimeLine(
            playerState = playerState,
            modifier = Modifier.weight(1f),
            timeLineRequester = timeLine,
            playPauseRequester = playPause
        )
    }
}


@Composable
fun Header(
    title: String,
    modifier: Modifier,
    style: TextStyle = TextStyle(
        color = Color.White,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        fontSynthesis = FontSynthesis.Style,
        fontFamily = FontFamily.Serif
    ),
    maxLines: Int = 2,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            style = style,
            maxLines = maxLines,
        )
    }
}

@Composable
fun CenterControls(
    modifier: Modifier,
    playerState: PlayerState,
    requesters: List<FocusRequester>,
) {
    var timeAction by remember { mutableStateOf(0L) }
    val timeLine = requesters[0]
    val skipPrevious = requesters[1]
    val playPause = requesters[2]
    val skipNext = requesters[3]
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconControl(
            visibility = playerState.exoPlayer.hasPreviousMediaItem(),
            playerState = playerState,
            icon = SkipPrevious,
            requester = skipPrevious,
            timeAction = timeAction,
            leftAction = {},
            rightAction = { playPause.requestFocus() },
            downAction = { if (playerState.videoDuration.value > 0L) timeLine.requestFocus() },
            clickAction = {
                playerState.visibleControlsRemain()
                playerState.exoPlayer.apply {
                    seekToPrevious()
                    if (!hasPreviousMediaItem()) playPause.requestFocus()
                }
            },
            updateTimeAction = { timeAction = it }
        )

        IconControl(
            visibility = true,
            playerState = playerState,
            icon = if (playerState.isPlaying.value) Pause else PlayArrow,
            requester = playPause,
            timeAction = timeAction,
            leftAction = { skipPrevious.requestFocus() },
            rightAction = { skipNext.requestFocus() },
            downAction = { if (playerState.videoDuration.value > 0L) timeLine.requestFocus() },
            clickAction = { playerState.togglePlayPause() },
            updateTimeAction = { timeAction = it }
        )

        IconControl(
            visibility = playerState.exoPlayer.hasNextMediaItem(),
            playerState = playerState,
            icon = SkipNext,
            requester = skipNext,
            timeAction = timeAction,
            leftAction = { playPause.requestFocus() },
            rightAction = {},
            downAction = { if (playerState.videoDuration.value > 0L) timeLine.requestFocus() },
            clickAction = {
                playerState.visibleControlsRemain()
                playerState.exoPlayer.apply {
                    seekToNext()
                    if (!hasNextMediaItem()) playPause.requestFocus()
                }
            },
            updateTimeAction = { timeAction = it }
        )
    }

    LaunchedEffect(Unit) {
        playPause.requestFocus()
    }
}

@Composable
fun IconControl(
    visibility: Boolean,
    playerState: PlayerState,
    icon: ImageVector,
    iconTint: Color = Color.White,
    iconSize: Dp = 70.dp,
    requester: FocusRequester,
    timeAction: Long,
    leftAction: () -> Unit,
    rightAction: () -> Unit,
    downAction: () -> Unit,
    clickAction: () -> Unit,
    updateTimeAction: (Long) -> Unit,
) {
    if (visibility) {
        var borderColor by remember { mutableStateOf(Color.Transparent) }

        IconButton(
            onClick = { clickAction() },
            modifier = Modifier
                .requiredSize(iconSize)
                .focusRequester(requester)
                .onFocusChanged {
                    borderColor = if (it.isFocused) iconTint
                    else Color.Transparent
                }
                .onKeyEvent {
                    if ((it.nativeKeyEvent.eventTime - timeAction) >= 200) {
                        playerState.visibleControlsRemain()
                        updateTimeAction(it.nativeKeyEvent.eventTime)
                        when (it.nativeKeyEvent.keyCode) {
                            KEYCODE_BACK -> {
                                playerState.hideControls()
                            }
                            KEYCODE_DPAD_LEFT -> {
                                leftAction()
                            }
                            KEYCODE_DPAD_RIGHT -> {
                                rightAction()
                            }
                            KEYCODE_DPAD_DOWN -> {
                                downAction()
                            }
                        }
                    }
                    true
                }
                .focusable()
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .requiredSize(iconSize),
            )
        }
    } else Spacer(modifier = Modifier.width(iconSize))

}

@Composable
fun TimeLine(
    playerState: PlayerState,
    progressColor: Color = Color.Red,
    modifier: Modifier,
    timeLineRequester: FocusRequester,
    playPauseRequester: FocusRequester,
) {

    var timeAction by remember { mutableStateOf(0L) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        if (playerState.videoDuration.value > 0L) {
            Text(
                text = "${
                    playerState.videoPosition.value.prettyTimestamp()
                }/${
                    playerState.videoDuration.value.prettyTimestamp()
                }",
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                //buffer
                Slider(
                    value = playerState.bufferPosition.value.toFloat(),
                    enabled = false,
                    onValueChange = { },
                    valueRange = 0f..playerState.videoDuration.value.toFloat(),
                    colors = SliderDefaults.colors(
                        disabledThumbColor = Color.Transparent,
                        disabledActiveTrackColor = Color.White
                    )
                )
                //seek bar
                Slider(
                    value = playerState.videoPosition.value.toFloat(),
                    onValueChange = {
                        playerState.movePosition(it.toLong())
                    },
                    valueRange = 0f..playerState.videoDuration.value.toFloat(),
                    colors = SliderDefaults.colors(
                        activeTrackColor = progressColor,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                        thumbColor = progressColor,
                    ),
                    modifier = Modifier
                        .focusRequester(timeLineRequester)
                        .onKeyEvent {
                            if (it.nativeKeyEvent.eventTime - timeAction >= 200) {
                                playerState.visibleControlsRemain()
                                timeAction = it.nativeKeyEvent.eventTime
                                when (it.nativeKeyEvent.keyCode) {
                                    KEYCODE_DPAD_CENTER -> {
                                        playerState.togglePlayPause()
                                    }
                                    KEYCODE_BACK -> {
                                        playerState.hideControls()
                                    }
                                    KEYCODE_DPAD_UP -> {
                                        playPauseRequester.requestFocus()
                                    }
                                    KEYCODE_DPAD_LEFT -> {
                                        playerState.controls.rewind()
                                    }
                                    KEYCODE_DPAD_RIGHT -> {
                                        playerState.controls.forward()
                                    }
                                }
                            }
                            true
                        }
                        .focusable()
                )
            }
        }
    }
}


