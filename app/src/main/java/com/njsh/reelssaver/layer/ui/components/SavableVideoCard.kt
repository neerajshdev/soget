package com.njsh.reelssaver.layer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.njsh.reelssaver.R

@Composable
fun SavableVideoCard(
    modifier: Modifier = Modifier,
    thumbnailUrl: String,
    onDownloadClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
            .padding(8.dp)
            .then(modifier)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(thumbnailUrl).crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shape = MaterialTheme.shapes.medium)
        )
        IconButton(
            onClick = onDownloadClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .wrapContentSize()
        ) {
            Icon(painterResource(
                id = R.drawable.ic_outlined_download),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = CircleShape
                ).padding(16.dp)
            )
        }
    }
}