package com.gd.reelssaver.networkimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import com.baat.net.image.ImageState
import com.gd.reelssaver.NetworkState


private val imageCache = ImageCache()


@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    uri: String,
    contentDescription: String?,
    placeholder: (@Composable () -> Unit)?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) {
    NetworkImage(
        modifier = modifier,
        uri = uri,
        loader = remember { DefaultImageLoader() },
        placeHolderContent = placeholder,
        networkState = NetworkState.Available,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}


@Composable
fun VideoThumbnail(
    modifier: Modifier = Modifier,
    uri: String,
    frameTimeStampUs: Long = 1000,
    contentDescription: String?,
    placeholder: (@Composable () -> Unit)?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) {
    NetworkImage(
        modifier = modifier,
        uri = uri,
        loader = remember { VideoThumbLoader(frameTimeStampUs) },
        placeHolderContent = placeholder,
        networkState = NetworkState.Available,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}


@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    uri: String,
    loader: NetworkImageLoader,
    placeHolderContent: (@Composable () -> Unit)?,
    networkState: NetworkState = NetworkState.Available,
    contentDescription: String?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality
) {
    BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
        val handler = rememberNetworkImageHandler(
            uri = uri,
            imageCache = imageCache,
            loader = loader,
            size = DpSize(maxWidth, maxHeight)
        )

        val state by handler.image()

        when (state) {
            is ImageState.Success -> {
                Image(
                    bitmap = (state as ImageState.Success).imageBitmap,
                    contentDescription = contentDescription,
                    alignment = alignment,
                    contentScale = contentScale,
                    alpha = alpha,
                    colorFilter = colorFilter,
                    filterQuality = filterQuality
                )
            }

            else -> {
                placeHolderContent?.invoke()
            }
        }

        if (networkState is NetworkState.Available) {
            if (state is ImageState.Failed) {
                // try to download again
                handler.start()
            }
        }

        DisposableEffect(key1 = handler) {
            handler.start()
            onDispose {
                handler.save()
            }
        }
    }
}


@Composable
fun rememberNetworkImageHandler(
    uri: String,
    loader: NetworkImageLoader,
    imageCache: ImageCache,
    size: DpSize
): NetworkImageHandler {
    return with(LocalDensity.current) {
        val intSize = IntSize(size.width.roundToPx(), size.height.roundToPx())
        remember(uri) {
            NetworkImageHandler(uri, imageCache, intSize, loader)
        }
    }
}





