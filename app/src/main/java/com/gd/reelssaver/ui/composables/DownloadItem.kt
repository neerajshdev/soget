package com.gd.reelssaver.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.desidev.downloader.model.Download
import com.gd.reelssaver.networkimage.VideoThumbnail
import com.gd.reelssaver.ui.theme.AppTheme
import com.gd.reelssaver.ui.util.debugLine
import io.ktor.http.ContentType
import java.time.LocalDateTime


@Preview
@Composable
fun DownloadItemPreview() {
    AppTheme {
        val item = remember {
            Download(
                id = 1,
                name = "sample.mp498908908908dfalj___8o787sample.mp498908908908dfalj___8o787",
                url = "https://scontent.fdel24-1.fna.fbcdn.net/v/t42.1790-2/409326485_314328708142241_242782183691640083_n.mp4?_nc_cat=110&ccb=1-7&_nc_sid=55d0d3&efg=eyJybHIiOjc3NywicmxhIjo4MzcsInZlbmNvZGVfdGFnIjoic3ZlX3NkIn0%3D&_nc_ohc=evseQJsxy20AX-ciKuL&_nc_rml=0&_nc_ht=scontent.fdel24-1.fna&oh=00_AfC4MPGodNlboNAGFCt-WPHAdIkwq5-ajriNfVTD5uT0qw&oe=6591C011",
                contentSize = 5000L,
                downloaded = 3000L,
                status = Download.Status.InProgress,
                type = ContentType.Video.MP4,
                time = LocalDateTime.now(),
                localPath = ""
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
    if (item.status == Download.Status.InProgress) {
        DownloadingItem(item = item, modifier = modifier)
    } else {
        DownloadedItem(item = item, modifier = modifier)
    }
}


@Composable
fun DownloadedItem(item: Download, modifier: Modifier) {
    Card(
        modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row {
            Box {
                VideoThumbnail(
                    uri = item.url,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(80.dp),
                    placeholder = {
                        Shimmer(modifier)
                    }
                )

                if (item.status == Download.Status.Complete) {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp)
                            .size(40.dp)
                    )
                }
            }

            Text(
                text = item.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}


@Composable
fun DownloadingItem(item: Download, modifier: Modifier) {
    val progressValue = item.downloaded / item.contentSize.toFloat()
    Card(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            VideoThumbnail(
                uri = item.url,
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .size(80.dp),
                placeholder = {
                    Shimmer(modifier)
                }
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Progress(progress = progressValue, modifier = Modifier)
            }
        }
    }
}

@Composable
fun Progress(progress: Float, modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (progress * 100).toInt().toString() + "%",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = { progress }
        )
    }
}
