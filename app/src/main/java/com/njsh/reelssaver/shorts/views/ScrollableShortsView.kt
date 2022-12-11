package com.njsh.reelssaver.shorts.views

import android.view.WindowManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.njsh.infinitelist.VerticalList
import com.njsh.infinitelist.isCloseToEnd
import com.njsh.reelssaver.MainActivity
import com.njsh.reelssaver.shorts.data.Repository
import com.njsh.reelssaver.shorts.room.ShortVideo


private const val TAG = "ScrollableShortsView.kt"

@Composable
fun ScrollableShorts() {
    RemoveStatusBar()
    var initialData by remember {
        mutableStateOf(emptyList<ShortVideo>())
    }

    LaunchedEffect(key1 = Unit) {
        initialData = Repository.get(0, 10)
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
                    val data = Repository.get(l.tail.pos + 1, 10)
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
    DisposableEffect(key1 = context) {
        var attr: WindowManager.LayoutParams? = null
        if (context is MainActivity) {
            attr = context.window.attributes
            context.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        onDispose {
            attr?.let {
                if (context is MainActivity) {
                    context.window.attributes = attr
                }
            }
        }
    }

}
