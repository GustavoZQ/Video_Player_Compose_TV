package com.animextv.bygdx.presentation.player.utils

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val PlayArrow: ImageVector
    get() {
        if (_playArrow != null) {
            return _playArrow!!
        }
        _playArrow = materialIcon(name = "Filled.PlayArrow") {
            materialPath {
                moveTo(8.0f, 5.0f)
                verticalLineToRelative(14.0f)
                lineToRelative(11.0f, -7.0f)
                close()
            }
        }
        return _playArrow!!
    }

private var _playArrow: ImageVector? = null
