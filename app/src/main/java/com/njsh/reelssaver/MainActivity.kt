package com.njsh.reelssaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.exoplayer2.ExoPlayer
import com.njsh.reelssaver.entity.ShortVideo
import com.njsh.reelssaver.ui.components.VideoPlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val short = ShortVideo.getDummy()

        setContent {
            VideoPlayerView(
                playerProvider = { ExoPlayer.Builder(this).build() }, shortVideo = short
            )
        }
    }
}