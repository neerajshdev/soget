package com.njsh.reelssaver.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.njsh.downloader.DownloadManager
import com.njsh.downloader.youtube.YtParser
import com.njsh.downloader.youtube.YtVideo
import com.njsh.downloader.youtube.YtVideoFormat
import com.njsh.reelssaver.R
import com.njsh.reelssaver.navigation.Page
import com.njsh.reelssaver.ui.components.InputUrlField
import com.njsh.reelssaver.util.AskForRWPerms
import com.njsh.reelssaver.util.UniqueId
import com.njsh.reelssaver.util.hasRWPerm

private const val TAG = "Youtube Page"


// constants
val dir = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DOWNLOADS}"

class PageYoutube : Page(pageTag = "Youtube Videos")
{
    private val inputUrlComp = InputUrlField()
    private val ytVideoComp = YoutubeVideo()

    init
    {
        inputUrlComp.onUrlInput = { text ->
            //Todo validate url text
            // here..
            Thread {
                try
                {
                    Log.d(TAG, ": Starting download Data")
                    val ytParser = YtParser(text)
                    val ytVideoData = ytParser.fetch()
                    val handler = android.os.Handler(Looper.getMainLooper())
                    handler.post {
                        ytVideoComp.ytVideoData.value = ytVideoData
                    }
                } catch (ex: Exception)
                {
                    ex.printStackTrace()
                }
            }.start()
        }

        addContent { this.Compose() }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun Compose()
    {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            inputUrlComp.Compose(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
            ytVideoComp.Compose()
        }
    }
}


class YoutubeVideo
{
    val ytVideoData = mutableStateOf<YtVideo?>(null)
    private var onDownloadClick: (YtVideoFormat, String) -> Unit
    lateinit var context: Context

    init
    {
        onDownloadClick = { ytVideoFormat, title ->
            val dManager = DownloadManager.getInstance()
            //TODO:    write code to download into a file
            val dir = Environment.getExternalStorageDirectory().absolutePath
            val filename = "$title." + ytVideoFormat.mimeType.substringAfter("/")

            dManager.addNewTask(ytVideoFormat.url, dir, filename, null, UniqueId.getUniqueId())
        }
    }

    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        context = LocalContext.current

        if (!hasRWPerm())
        {
            AskForRWPerms(onAccept = { Log.d(TAG, "Compose: RW  permissions accepted") }) {
                // todo: handle permissions rejection
            }
        }

        if (ytVideoData.value != null)
        {
            val ytVideo: YtVideo = ytVideoData.value!!

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.verticalScroll(state = rememberScrollState())
            ) {
                VideoThumb(url = ytVideo.detail.thumbnails.last().url)
                Text(
                    text = ytVideo.detail.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                // available formats
                for (format in ytVideo.formats)
                {
                    AvailableFormats(format = format, ytVideo.detail.title)
                }
            }
        }
    }

    @Composable
    fun VideoThumb(url: String)
    {
        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            Log.d(TAG, "thumb url $url")
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Text(
                text = "00:00", modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }

    @Composable
    private fun AvailableFormats(format: YtVideoFormat, title: String)
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.primary)
                .padding(horizontal = 8.dp)
        ) {
            val textColor = MaterialTheme.colors.onPrimary
            Text(text = format.mimeType, color = textColor, modifier = Modifier.weight(1f))
            Text(text = format.quality, color = textColor, modifier = Modifier.weight(1f))
            IconButton(onClick = { onDownloadClick(format, title) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_downloaad),
                    contentDescription = null,
                    tint = textColor
                )
            }
        }
    }
}