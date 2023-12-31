package com.gd.reelssaver.networkimage

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

interface NetworkImageLoader {
    suspend fun load(uri: String, size: IntSize): ImageBitmap?

}