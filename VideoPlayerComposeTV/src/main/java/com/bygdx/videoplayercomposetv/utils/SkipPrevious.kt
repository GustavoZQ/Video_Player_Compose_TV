package com.animextv.bygdx.presentation.player.utils

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val SkipPrevious: ImageVector
    get() {
        if (_skipPrevious != null) {
            return _skipPrevious!!
        }
        _skipPrevious = materialIcon(name = "Filled.SkipPrevious") {
            materialPath {
                moveTo(6.0f, 6.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(12.0f)
                lineTo(6.0f, 18.0f)
                close()
                moveTo(9.5f, 12.0f)
                lineToRelative(8.5f, 6.0f)
                lineTo(18.0f, 6.0f)
                close()
            }
        }
        return _skipPrevious!!
    }

private var _skipPrevious: ImageVector? = null