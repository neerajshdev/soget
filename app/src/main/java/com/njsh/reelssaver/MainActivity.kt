package com.njsh.reelssaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.njsh.reelssaver.shorts.room.ShortVideo
import com.njsh.reelssaver.shorts.views.VideoPlayerState
import kotlin.random.Random

private const val TAG = "MainActivity.kt"

data class ShortVideoState(
    val short: ShortVideo, val viewState: VideoPlayerState = VideoPlayerState()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val colors = mutableListOf(Color.Red, Color.Blue, Color.Gray, Color.Green)

        setContent() {
            val random = Random(System.currentTimeMillis())
            Pager(
                contentModifier = Modifier
                    .fillMaxSize()
            ) { index ->
                if (colors.lastIndex > index) {
                    colors.add(Color(random.nextInt()))
                }
                Box(modifier = Modifier.fillMaxSize().background(color = colors[index]), contentAlignment = Alignment.Center) {
                    Text(text = "$index")
                }
            }
        }
    }
}

