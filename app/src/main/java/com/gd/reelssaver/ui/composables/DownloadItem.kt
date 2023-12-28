package com.gd.reelssaver.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.theme.AppTheme
import io.ktor.http.ContentType
import java.time.LocalDate


/*val model = ImageRequest.Builder(context)
    .data(uri)
    .videoFrameMillis(10000)
    .decoderFactory { result, options, _ ->
        VideoFrameDecoder(
            result.source,
            options
        )
    }
    .build()*/

@Preview
@Composable
fun DownloadItemPreview() {
    AppTheme {
        val item = remember {
            Download(
                1,
                "sample.mp4",
                "https://scontent.fdel24-1.fna.fbcdn.net/v/t42.1790-2/409326485_314328708142241_242782183691640083_n.mp4?_nc_cat=110&ccb=1-7&_nc_sid=55d0d3&efg=eyJybHIiOjc3NywicmxhIjo4MzcsInZlbmNvZGVfdGFnIjoic3ZlX3NkIn0%3D&_nc_ohc=evseQJsxy20AX-ciKuL&_nc_rml=0&_nc_ht=scontent.fdel24-1.fna&oh=00_AfC4MPGodNlboNAGFCt-WPHAdIkwq5-ajriNfVTD5uT0qw&oe=6591C011",
                "",
                5000L,
                3000L,
                status = Download.Status.InProgress,
                type = ContentType.Video.MP4,
                time = LocalDate.now()
            )
        }

        val item2 = item.copy(status = Download.Status.Complete)

        Surface {
            Column {
                DownloadItem(
                    item = item, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                DownloadItem(
                    item = item2, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun DownloadItem(item: Download, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val progressValue = item.downloaded / item.contentSize.toFloat()
    Column(modifier) {
        // video thumbnail
        if (item.type == ContentType.Video.Any) {
            val model = ImageRequest.Builder(context)
                .data(item.url)
                .videoFrameMillis(10000)
                .decoderFactory { result, options, _ ->
                    VideoFrameDecoder(
                        result.source,
                        options
                    )
                }
                .build()

            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            val icon = if (item.status == Download.Status.InProgress) {
                Icons.Rounded.Downloading
            } else {
                Icons.Rounded.DownloadDone
            }

            Icon(imageVector = icon, contentDescription = null)
            Progress(filename = item.name, value = progressValue)
            Text(text = (progressValue * 100).toInt().toString() + "%")
        }
    }
}


@Composable
fun Progress(filename: String, value: Float) {
    Column {
        Text(text = filename, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(progress = { value })
    }
}