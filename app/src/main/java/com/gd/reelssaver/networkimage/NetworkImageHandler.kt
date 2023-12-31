package com.gd.reelssaver.networkimage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import com.baat.net.image.ImageState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkImageHandler(
    private val uri: String,
    private val imageCache: ImageCache,
    private val size: IntSize,
    private val loader: NetworkImageLoader,
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val imageState = mutableStateOf<ImageState>(ImageState.WaitingForAction)

    fun start() {
        scope.launch {
            try {
                imageState.value = ImageState.Loading
                // check if image is already in cache
                var imageBitmap = imageCache.get(uri)?.asImageBitmap()
                if (imageBitmap == null) {
                    withContext(Dispatchers.IO) {
                        println("downloading image from $uri..")
                        imageBitmap = loader.load(uri, size)
                        println("image downloaded from $uri $imageBitmap")
                    }
                }

                imageState.value = ImageState.Success(imageBitmap!!)
            } catch (ex: Exception) {
                imageState.value = ImageState.Failed(ex)
                println(ex)
            }
        }
    }

    fun image(): State<ImageState> = imageState

    fun save() {
        scope.launch {
            val state = imageState.value
            if (state is ImageState.Success) {
                imageCache.save(state.imageBitmap.asAndroidBitmap(), uri)
            }
        }
    }
}