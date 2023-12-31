package com.baat.net.image

import androidx.compose.ui.graphics.ImageBitmap

sealed class ImageState {
    object WaitingForAction : ImageState()
    object WaitingForConnection: ImageState()
    object Loading: ImageState()
    data class Success(val imageBitmap: ImageBitmap): ImageState()
    data class Failed(val throwable: Throwable): ImageState()
}