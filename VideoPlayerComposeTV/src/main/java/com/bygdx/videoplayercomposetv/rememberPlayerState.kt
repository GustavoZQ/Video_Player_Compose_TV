package com.bygdx.videoplayercomposetv

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberPlayerState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    forwardIncrementSeconds: Int = 15,
    backIncrementSeconds: Int = 15,
): PlayerStateImpl = remember {
    PlayerStateImpl(
        exoPlayer = ExoPlayer.Builder(context).apply {
            setSeekForwardIncrementMs((forwardIncrementSeconds * 1000).toLong())
            setSeekBackIncrementMs((backIncrementSeconds * 1000).toLong())
        }.build(),
        coroutineScope = coroutineScope,
    ).also {
        it.exoPlayer.addListener(it)
    }
}