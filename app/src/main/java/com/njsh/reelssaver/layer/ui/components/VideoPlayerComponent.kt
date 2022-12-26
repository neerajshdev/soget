package com.njsh.reelssaver.layer.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.njsh.reelssaver.R
import com.njsh.reelssaver.layer.domain.models.ShortVideoModel
import com.njsh.reelssaver.util.toPrettyNum
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val TAG = "VideoPlayerView.kt"


@Preview
@Composable
fun VideoPlayerComponentP() {
    ShortVideoPlayerComponent(shortVideo = ShortVideoModel.createFakeModel(),
        modifier = Modifier.fillMaxSize(),
        isPlaying = false,
        onDownloadClick = {},
        onLikeClick = {},
        onShareClick = {})
}

@Composable
fun ShortVideoPlayerComponent(
    modifier: Modifier = Modifier, shortVideo: ShortVideoModel, isPlaying: Boolean,
    onDownloadClick: (ShortVideoModel) -> Unit,
    onShareClick: (ShortVideoModel) -> Unit,
    onLikeClick: (ShortVideoModel) -> Unit,
) {
    var progress by remember { mutableStateOf(0f) }
    var isFirstFrameRendered by remember { mutableStateOf(false) }

    Box(modifier = modifier.background(Color.Black)) {
        if (isPlaying) {
            ExoPlayer(url = shortVideo.mpdUrl,
                bgColor = Color.Black,
                modifier = Modifier.fillMaxSize(),
                onProgressUpdate = { progress = it },
                onRenderFirstFrame = {
                    isFirstFrameRendered = true
                })
        }

        if (!isPlaying || !isFirstFrameRendered) AsyncImage(
            model = shortVideo.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // UI over the video surface
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.BottomCenter)
        ) {
            EdgeIconsView(onHeartClick = { onLikeClick(shortVideo) },
                onDownloadClick = { onDownloadClick(shortVideo) },
                onShareClick = { onShareClick(shortVideo) },
                heartIcon = { painterResource(id = R.drawable.ic_filled_heart) },
                shareIcon = { painterResource(id = R.drawable.ic_filled_share) },
                downloadIcon = { painterResource(id = R.drawable.ic_filled_download) },
                likes = shortVideo.likes.toPrettyNum(),
                modifier = Modifier.align(Alignment.End)
            )
            ProgressBar(progressProvider = { progress }, modifier = Modifier.fillMaxWidth())
            Text(
                text = shortVideo.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(min = 56.dp, max = 56.dp)
            )
        }
    }
}

@Composable
private fun ExoPlayer(
    modifier: Modifier = Modifier,
    url: String,
    bgColor: Color = Color.Black,
    onProgressUpdate: ((Float) -> Unit)? = null,
    isLoadingChange: ((Boolean) -> Unit)? = null,
    onRenderFirstFrame: (() -> Unit)? = null
) {
    val localLifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    var isPlaying by remember { mutableStateOf(false) }

    fun togglePlay() {
        if (exoPlayer.isPlaying) exoPlayer.pause()
        else exoPlayer.play()
    }

    AndroidView(factory = { ctx ->
        val view = LayoutInflater.from(ctx).inflate(R.layout.exo_player_view, null) as StyledPlayerView
        view.apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            useController = false
            player = exoPlayer
        }
    }, modifier = modifier
        .background(color = bgColor)
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                togglePlay()
            })
        })

    DisposableEffect(key1 = exoPlayer, effect = {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(state: Boolean) {
                isPlaying = state
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                if (isLoadingChange != null) {
                    isLoadingChange(isLoading)
                }
            }

            override fun onRenderedFirstFrame() {
                onRenderFirstFrame?.invoke()
            }
        }
        try {
            exoPlayer.addListener(listener)
            exoPlayer.addMediaItem(MediaItem.fromUri(url))
            exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            exoPlayer.prepare()
            exoPlayer.play()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        val lifecycleObserver = object : LifecycleEventObserver {
            var wasPlaying = false
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        wasPlaying = exoPlayer.isPlaying
                        if (wasPlaying) {
                            exoPlayer.pause()
                        }
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        if (wasPlaying) {
                            exoPlayer.play()
                        }
                    }
                    else -> {}
                }
            }
        }

        localLifecycle.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.pause()
            exoPlayer.release()

            localLifecycle.lifecycle.removeObserver(lifecycleObserver)
        }
    })

    if (isPlaying) {
        LaunchedEffect(key1 = exoPlayer, block = {
            while (isActive) {
                if (exoPlayer.duration != C.TIME_UNSET) {
                    val newValue = (exoPlayer.currentPosition / exoPlayer.duration.toFloat())
                    if (onProgressUpdate != null) {
                        onProgressUpdate(newValue)
                    }
                }
                delay(100)
            }
        })
    }
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
}


@Composable
private fun IconButton(
    painter: Painter, label: String?, onClick: () -> Unit, color: Color
) {
    androidx.compose.material3.IconButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painter = painter, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(4.dp))
            label?.let {
                Text(text = label, style = MaterialTheme.typography.bodySmall, color = color)
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun ProgressBar(modifier: Modifier = Modifier, progressProvider: () -> Float) {
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