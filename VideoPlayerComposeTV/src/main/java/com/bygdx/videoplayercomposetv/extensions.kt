package com.bygdx.videoplayercomposetv

import java.util.concurrent.TimeUnit

fun Long.prettyTimestamp(): String {
    return if (TimeUnit.MILLISECONDS.toMinutes(this) >= 60) {
        String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(this)
            ),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}