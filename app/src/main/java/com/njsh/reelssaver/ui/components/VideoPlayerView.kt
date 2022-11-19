package com.njsh.reelssaver.ui.components

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.njsh.reelssaver.R
import com.njsh.reelssaver.entity.ShortVideo
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val TAG = "VideoPlayerView.kt"


@Composable
fun VideoPlayerView(
    modifier: Modifier = Modifier,
    playerProvider: () -> ExoPlayer,
    shortVideo: ShortVideo,
) {
    Log.d(TAG, "VideoPlayerView()")
    val lifecycleOwner = LocalLifecycleOwner.current

    val player = remember {
        playerProvider()
    }

    var progress by remember {
        mutableStateOf(0f)
    }


    fun playVideo() = player.play()
    fun pauseVideo() = player.pause()

    fun togglePlay() {
        if (player.isPlaying) {
            pauseVideo()
        } else {
            playVideo()
        }
    }


    DisposableEffect(key1 = player) {
        val listener = object : LifecycleEventObserver {
            var wasPlaying = false
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (wasPlaying || player.isPlaying) when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        playVideo(); wasPlaying = false
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        pauseVideo(); wasPlaying = true
                    }
                    else -> {}
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(listener)

        player.addMediaItem(MediaItem.fromUri(shortVideo.mpdUrl))
        player.prepare()
        playVideo()

        onDispose {
            player.release()
            lifecycleOwner.lifecycle.removeObserver(listener)
        }
    }

    Box(modifier = modifier.background(Color.Black)) {
        VideoView(exoPlayer = player,
            bgColor = Color.Black,
            modifier = Modifier.fillMaxSize(),
            onClick = { togglePlay() })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.BottomCenter)
        ) {
            EdgeIconsView(onHeartClick = { /*TODO*/ },
                onDownloadClick = { /*TODO*/ },
                onShareClick = { /*TODO*/ },
                heartIcon = { painterResource(id = R.drawable.ic_filled_heart) },
                shareIcon = { painterResource(id = R.drawable.ic_filled_share) },
                downloadIcon = { painterResource(id = R.drawable.ic_filled_download) },
                likes = shortVideo.likes,
                modifier = Modifier.align(Alignment.End)
            )
            ProgressBar(progressProvider = { progress }, modifier = Modifier.fillMaxWidth())
            Text(
                text = shortVideo.label,
                style = MaterialTheme.typography.subtitle2,
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(min = 56.dp, max = 56.dp)
            )
        }
    }

    UpdateProgress(player = player, onUpdate = { progress = it })
}

@Composable
private fun VideoView(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    onClick: () -> Unit,
    bgColor: Color = Color.Black,
) {
    Log.d(TAG, "VideoView()")
    AndroidView(factory = { context ->
        val view = LayoutInflater.from(context)
            .inflate(R.layout.exo_player_view, null) as StyledPlayerView
        view.apply {
            layoutParams =
                ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            useController = false
        }
    }, update = {
        it.player = exoPlayer
    }, modifier = modifier
        .background(color = bgColor)
        .clickable(onClick = onClick))
}


@Composable
private fun EdgeIconsView(
    modifier: Modifier,
    onHeartClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit,
    heartIcon: @Composable () -> Painter,
    shareIcon: @Composable () -> Painter,
    downloadIcon: @Composable () -> Painter,
    fgColor: Color = Color.White,
    likes: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(end = 16.dp)
    ) {
        IconButton(
            painter = heartIcon(), label = likes, onClick = onHeartClick, color = fgColor
        )
        IconButton(
            painter = shareIcon(), label = "share", onClick = onShareClick, color = fgColor
        )
        IconButton(
            painter = downloadIcon(), label = "save", onClick = onDownloadClick, color = fgColor
        )
    }
    Spacer(modifier = Modifier.height(32.dp))

    Log.d(TAG, "EdgeIconsView()")
}


@Composable
private fun IconButton(
    painter: Painter, label: String?, onClick: () -> Unit, color: Color
) {
    androidx.compose.material.IconButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painter = painter, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(4.dp))
            label?.let {
                Text(text = label, style = MaterialTheme.typography.body1, color = color)
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun ProgressBar(modifier: Modifier = Modifier, progressProvider: () -> Float) {
    Log.d(TAG, "ProgressBar()")
    Canvas(
        modifier = modifier
    ) {
        drawLine(
            color = Color.White,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 4f
        )
        drawLine(
            color = Color.Red,
            start = Offset(0f, 0f),
            end = Offset(size.width * progressProvider(), 0f),
            strokeWidth = 6f
        )
    }
}

@Composable
private fun UpdateProgress(
    player: ExoPlayer, onUpdate: (Float) -> Unit
) {
    Log.d(TAG, "UpdateProgress()")
    var isPlaying by remember {
        mutableStateOf(false)
    }

    if (isPlaying) LaunchedEffect(Unit) {
        while (isActive) {
            if (player.duration != C.TIME_UNSET) {
                val newValue = (player.currentPosition.toFloat() / player.duration)
                onUpdate(newValue)
            }
            delay(100)
        }
    }

    DisposableEffect(key1 = player, effect = {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(value: Boolean) {
                isPlaying = value
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
        }
    })
}
