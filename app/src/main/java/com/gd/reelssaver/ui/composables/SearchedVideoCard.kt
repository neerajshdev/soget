package com.gd.reelssaver.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gd.reelssaver.ui.theme.AppTheme
import com.gd.reelssaver.util.getVideoThumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Preview
@Composable
fun SearchedVideoCardPrev() {
    val videoData = remember {
        com.gd.reelssaver.model.VideoData(
            dashManifest = "",
            videoUrl = "",
            imageUrl = "https://scontent.fdel24-1.fna.fbcdn.net/v/t15.5256-10/368062140_869048418169205_4902966137490692257_n.jpg?stp=dst-webp_e15_q70_s800x1425_tt1_u&efg=eyJ1cmxnZW4iOiJ1cmxnZW5fZnJvbV91cmwifQ&_nc_cid=0&_nc_ad=z-m&_nc_rml=0&_nc_ht=scontent.fdel24-1.fna&_nc_cat=106&_nc_ohc=ZH87ORG5oGYAX-07haS&ccb=1-7&_nc_sid=1a7029&oh=00_AfDXXSSl28XZ0wfWP6pLPECBycDfoNtxOuotKoScARpe6w&oe=65561EC7"
        )
    }

    AppTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            SearchVideoCard(videoData = videoData, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun SearchVideoCard(
    modifier: Modifier = Modifier,
    videoData: com.gd.reelssaver.model.VideoData,
    onDownloadClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        if (videoData.imageUrl != null) {
            AsyncImage(
                model = videoData.imageUrl,
                contentDescription = "video thumbnail",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(220.dp)
                    .align(Alignment.Center)
            )
        } else {
            var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
            LaunchedEffect(key1 = videoData) {
                withContext(Dispatchers.IO) {
                    bitmap = getVideoThumbnail(videoData.videoUrl)?.asImageBitmap()
                }
            }
            bitmap?.let {
                Image(
                    bitmap = it, contentDescription = null, contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(220.dp)
                        .align(Alignment.Center)
                )
            }
        }

        FilledIconButton(
            onClick = onDownloadClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Download, contentDescription = "download video")
        }
    }
}