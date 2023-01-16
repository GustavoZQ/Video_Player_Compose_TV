package com.animextv.bygdx.presentation.player.utils

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val SkipNext: ImageVector
    get() {
        if (_skipNext != null) {
            return _skipNext!!
        }
        _skipNext = materialIcon(name = "Filled.SkipNext") {
            materialPath {
                moveTo(6.0f, 18.0f)
                lineToRelative(8.5f, -6.0f)
                lineTo(6.0f, 6.0f)
                verticalLineToRelative(12.0f)
                close()
                moveTo(16.0f, 6.0f)
                verticalLineToRelative(12.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(6.0f)
                horizontalLineToRelative(-2.0f)
                close()
            }
        }
        return _skipNext!!
    }

private var _skipNext: ImageVector? = null
