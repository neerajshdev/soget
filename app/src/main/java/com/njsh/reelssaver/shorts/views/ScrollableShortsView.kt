package com.njsh.reelssaver.shorts.views

import android.view.WindowManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.njsh.infinitelist.VerticalList
import com.njsh.infinitelist.isCloseToEnd
import com.njsh.reelssaver.MainActivity
import com.njsh.reelssaver.shorts.data.ShortVideoRepo
import com.njsh.reelssaver.layer.data.room.ShortVideoEntity


private const val TAG = "ScrollableShortsView.kt"

@Composable
fun ScrollableShortVideos() {
    RemoveStatusBar()
    var initialData by remember {
        mutableStateOf(emptyList<ShortVideoEntity>())
    }

    LaunchedEffect(key1 = Unit) {
        ShortVideoRepo.clear()
        initialData = ShortVideoRepo.get(0, 10)
    }

    if (initialData.isNotEmpty()) {
        VerticalList {
            items(initialData) {
                var isCenter by remember {
                    mutableStateOf(false)
                }
                VideoPlayerView(shortVideo = it, isSelected = isCenter)

                onLayout {
                    isCenter = viewport.height / 2 in y..y + height
                }
            }

            onEndOfFrame {
                if (l.isCloseToEnd()) {
                    val data = ShortVideoRepo.get(l.tail.pos + 1, 10)
                    l.addAll(data)
                    println("linkedlist = ${l.format()}")
                }
            }
        }
    }
}

@Composable
private fun RemoveStatusBar() {
    val context = LocalContext.current
    if (context is MainActivity) {
        DisposableEffect(key1 = context) {
            val attr = context.window.attributes
            context.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            onDispose {
                context.window.attributes = attr
            }
        }
    }
}
