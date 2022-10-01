package com.njsh.myapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njsh.downloader.DownloadManager
import com.njsh.downloader.DownloadTask
import com.njsh.downloader.UpdateListener
import com.njsh.myapp.R
import com.njsh.myapp.ui.theme.MyappTheme


class DownloadControl(
    completed: Int,
    toBeComplete: Int,
    speed: Int,
    state: DownloadTask.State,
    private val textColor: Color = Color.Black,
)
{
    val completed = mutableStateOf(completed)
    val toBeComplete = mutableStateOf(toBeComplete)
    val speed = mutableStateOf(speed)
    val state = mutableStateOf(state)

    // events
    var onCancel: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onPlay: (() -> Unit)? = null

    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = completed.value / toBeComplete.value.toFloat(),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (state.value == DownloadTask.State.Downloading)
                    {
                        onPause?.invoke()
                    } else
                    {
                        onPlay?.invoke()
                    }
                }) {
                    val painter =
                        if (state.value == DownloadTask.State.Downloading) painterResource(id = R.drawable.ic_baseline_pause_24)
                        else painterResource(id = R.drawable.ic_baseline_play_arrow_24)
                    Icon(painter = painter, contentDescription = null)
                }
                IconButton(onClick = { onCancel?.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_cancel_24),
                        contentDescription = null
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${speed.value} MBs", color = textColor, modifier = Modifier.weight(1f))
                Text(text = "${completed.value}/${toBeComplete.value}", color = textColor)
            }
        }
    }
}


@Preview
@Composable
fun PrevProgress()
{
    val onBackgroundColor = MaterialTheme.colors.onBackground
    val progress = remember {
        DownloadControl(
            completed = 20,
            toBeComplete = 100,
            speed = 5,
            textColor = onBackgroundColor,
            state = DownloadTask.State.Downloading
        )
    }

    MyappTheme {
        progress.Compose(
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}


class AllDownloadsPage
{
    @Composable
    fun Compose()
    {
        val downloadsTask = DownloadManager.getInstance().tasks
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            for (task in downloadsTask)
            {
                val colorOnBg = MaterialTheme.colors.onBackground
                val taskControl = remember {
                    DownloadControl(
                        completed = (task.bytes / 10_485_76).toInt(),
                        toBeComplete = (task.contentSize / 10_485_76).toInt(),
                        speed = 0,
                        state = task.state,
                        textColor = colorOnBg
                    )
                }

                DisposableEffect(key1 = taskControl) {
                    task.setUpdateListener(object : UpdateListener()
                    {
                        override fun onDownloadFished()
                        {
                            taskControl.state.value = DownloadTask.State.Finished
                        }

                        override fun onUpdateProgress(complete: Long, total: Long)
                        {
                            taskControl.completed.value = toMB(complete)
                            taskControl.toBeComplete.value = toMB(total)
                        }

                        override fun onUpdateSpeed(bytesPerSec: Int)
                        {
                            taskControl.speed.value = toMB(bytesPerSec.toLong())
                        }
                    })
                    onDispose {
                        task.setUpdateListener(null)
                    }
                }
            }
        }
    }


    private fun toMB(bytes: Long) : Int
    {
        return (bytes / 10_485_76).toInt()
    }
}


