package com.centicbhaiya.getitsocial.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.centicbhaiya.getitsocial.R
import com.centicbhaiya.getitsocial.model.DownloadProgress
import com.centicbhaiya.getitsocial.ui.theme.AppTheme
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status


@Preview
@Composable
fun DownloadProgressCardPrev() {
    AppTheme {
        DownloadProgressCard(
            DownloadProgress(
                progress = 50,
                label = "example file.mp4",
                id = 98908
            )
        )
    }
}

@Composable
fun DownloadsScreen(downloads: List<Download>) {
    val inProgress =
        downloads.filter { it.status == Status.DOWNLOADING || it.status == Status.ADDED }.map {
            DownloadProgress(
                id = it.id,
                label = it.file,
                progress = it.progress
            )
        }

    Column {
        inProgress.forEach {
            key(it.id) {
                DownloadProgressCard(download = it)
            }
        }
    }
}

@Composable
fun DownloadProgressCard(download: DownloadProgress) {
    Card {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(8.dp)
                    ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_download_24),
                    contentDescription = null
                )
            }

            ProgressIndicator(
                download.label,
                progress = download.progress / 100f,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )
            Text(
                text = "${download.progress}%",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Bottom)
            )

            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
            }
        }
    }
}


@Composable
fun ProgressIndicator(name: String, progress: Float, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}