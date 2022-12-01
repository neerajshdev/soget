package com.njsh.reelssaver

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.njsh.reelssaver.shorts.data.LocalSource
import com.njsh.reelssaver.shorts.data.PagedShortVideos
import com.njsh.reelssaver.shorts.room.ShortVideo
import com.njsh.reelssaver.shorts.views.VideoPlayerState
import com.njsh.reelssaver.shorts.views.VideoPlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity.kt"

data class ShortVideoState(
    val short: ShortVideo, val viewState: VideoPlayerState
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       /* setContent {
            var pageIndex = 0
            val scope = rememberCoroutineScope()

            val state = rememberPagerState(emptyList<ShortVideoState>()).apply {
                orientation = Orientation.Vertical
            }

            Pager(
                state = state,
                modifier = Modifier.fillMaxSize(),
                onItemSelect = { oldIndex, index, item ->
                    Log.d(TAG, "onItemSelect: $index")

                    state.items[oldIndex].viewState.also {
                        it.player?.release()
                        it.player = null
                    }

                    item.viewState.player = ExoPlayer.Builder(this@MainActivity).build().apply {
                        setMediaItem(MediaItem.fromUri(item.short.mpdUrl))
                    }
                    item.viewState.play()


                    // load previous content again
                    if (index == 0 && PagedShortVideos.current != 0)  {

                    }

                    // more content
                    if (index == state.items.lastIndex) {
                        scope.launch(Dispatchers.IO) {
                            val data = PagedShortVideos.run {
                                next()
                                getPage()
                            }

                            val items = state.items
                            items.addAll(data.map {
                                ShortVideoState(
                                    short = it, viewState = VideoPlayerState()
                                )
                            })

                            if (items.size > PagedShortVideos.pageSize * 2) {
                                items.removeRange(0, PagedShortVideos.pageSize)
                            }
                        }
                    }
                },
                contentBuilder = { index, shortVideoState ->
                    Log.d(TAG, "onCreate: index = $index, VideoPlayerView() ")
                    VideoPlayerView(
                        shortVideo = shortVideoState.short,
                        state = shortVideoState.viewState,
                        modifier = Modifier.fillMaxSize()
                    )
                })

            LaunchedEffect(PagedShortVideos){
                PagedShortVideos.deleteAll()
                withContext(Dispatchers.IO) {
                    val items = state.items
                    val shortVideo = PagedShortVideos.getPage().map { ShortVideoState(it, VideoPlayerState()) }
                    items.addAll(shortVideo)

                    withContext(Dispatchers.Main) {
                        items[state.currentIndex].viewState.player = ExoPlayer.Builder(this@MainActivity).build().apply {
                            setMediaItem(MediaItem.fromUri(state.items[state.currentIndex].short.mpdUrl))
                            prepare()
                            play()
                        }
                    }
                }
            }


            LazyRow() {
                items(items = listOf("0")) { item ->

                }
            }
        }*/

        val color = listOf(Color.Cyan, Color.Blue, Color.Red, Color.DarkGray)

        setContent {
            Box(contentAlignment = Alignment.Center) {
                Pager(itemFraction = 0.5f, itemSpacing = 200) {
                    range(range = 0..3) {
                        Log.d(TAG, "Box($it)")
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = color[it])
                        )
                    }

                    onItemSelect = { index ->
                        Log.d(TAG, "onItemSelect = $index")
                    }

                    onItemDeSelect = { index ->
                        Log.d(TAG, "onItemDeSelect = $index")
                    }
                }
                Box(modifier = Modifier.size(2.dp).background(color= Color.Black))
            }
        }
    }
}

