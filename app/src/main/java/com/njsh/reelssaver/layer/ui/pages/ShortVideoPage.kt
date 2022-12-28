package com.njsh.reelssaver.layer.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.njsh.infinitelist.VerticalList
import com.njsh.infinitelist.isCloseToEnd
import com.njsh.reelssaver.layer.domain.models.ShortVideoModel
import com.njsh.reelssaver.layer.ui.UiState
import com.njsh.reelssaver.layer.ui.components.ShortVideoPlayerComponent
import kotlinx.coroutines.sync.Mutex

private const val TAG = "ShortVideoPage.kt"
private var isLoadingData = false
private var mutex = Mutex()

@Composable
fun ShortVideoPage(shortVideoState: UiState.ShortVideoState) {
    val context = LocalContext.current
    var initialData by remember { mutableStateOf(emptyList<ShortVideoModel>()) } // load initial short videos
    LaunchedEffect(shortVideoState) {
        initialData = shortVideoState.loadVideos(0, 10)
    }

    if (initialData.isNotEmpty()) {
        VerticalList(modifier = Modifier.fillMaxSize()) {
            items(initialData) { model: ShortVideoModel ->
                var isPlaying by remember { mutableStateOf(false) }

                Box {
                    ShortVideoPlayerComponent(shortVideo = model,
                        isPlaying = isPlaying,
                        onDownloadClick = { shortVideoState.download(model) },
                        onShareClick = { shortVideoState.share(model, context) },
                        onLikeClick = {})

                    Text(
                        text = "${model.id}",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                onLayout {
                    isPlaying = viewport.height / 2 in y..y + height
                }
            }

            this.onEndOfFrame {
                mutex.lock()
                if (l.isCloseToEnd(1) && !isLoadingData) {
                    isLoadingData = true
                    //Log.d(TAG, "ShortVideoPage: last(${l.pos}) is closed to end(${l.tail.pos}) by diff = ${l.tail.pos - l.pos}")
                    val offset = l.tail.pos + 1
                    //Log.d(TAG, "ShortVideoPage: loading more data: offset($offset) size (${l.size})")
                    val data = shortVideoState.loadVideos(offset.toLong(), 10)
                    l.addAll(data)
                    // Log.d(TAG, "ShortVideoPage: loaded data: size(${l.size})")
                    isLoadingData = false
                }
                mutex.unlock()
            }
        }
    }
}