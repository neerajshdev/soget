package com.gd.reelssaver.ui.composables


import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.NativeAdLoader
import com.gd.reelssaver.ui.theme.AppTheme
import com.gd.reelssaver.ui.util.ComposeDebug
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Preview
@Composable
fun MediumSizeNativeAdPreview() {
    AppTheme {
        MediumSizeNativeAd {
            Image(
                painter = painterResource(id = R.drawable.ad_placeholder),
                contentDescription = "ad_placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun MediumSizeNativeAd(
    modifier: Modifier = Modifier,
    refreshTimeSec: Int = 60,
    adPlaceHolder: @Composable () -> Unit
) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(nativeAd) {
        onDispose {
            nativeAd?.destroy()
        }
    }

    LaunchedEffect(refreshTimeSec) {
        val time = refreshTimeSec * 1000L
        while (isActive) {
            nativeAd = NativeAdLoader.takeAndLoad()
            delay(time)
        }
    }

    MediumNativeAdContent(
        modifier = modifier,
        nativeAdProvider = { nativeAd },
        placeholderContent = {
            adPlaceHolder()
        }
    )
}


@Composable
private fun MediumNativeAdContent(
    modifier: Modifier = Modifier,
    nativeAdProvider: () -> NativeAd?,
    placeholderContent: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val nativeAdPlaceable = subcompose("native_ad") {
            AndroidNativeAdView(nativeAdProvider = nativeAdProvider)
        }.map {
            it.measure(constraints)
        }

        val maxSize =
            nativeAdPlaceable.maxByOrNull { it.width * it.height }?.run { IntSize(width, height) }
                ?: IntSize.Zero

        val placeholder = subcompose("place_holder") {
            val nativeAd = nativeAdProvider()
            if (nativeAd == null) {
                placeholderContent()
            }
        }.map {
            it.measure(
                Constraints.fixed(maxSize.width, maxSize.height)
            )
        }

        layout(maxSize.width, maxSize.height) {
            placeholder.forEach {
                it.place(0, 0)
            }
            
            nativeAdPlaceable.forEach {
                it.place(0, 0)
            }
        }
    }
}


@Composable
private fun AndroidNativeAdView(modifier: Modifier = Modifier, nativeAdProvider: () -> NativeAd?) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val nativeAd = nativeAdProvider()

    ComposeDebug(dbgStr = "nativeAd: $nativeAd")

    AndroidView(
        modifier = modifier,
        factory = {
            LayoutInflater.from(it)
                .inflate(R.layout.medium_size_native_ad_view, null)
                .apply {
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT,
                    )
                }
        },
        update = { view ->
            if (nativeAd != null) {
                view.visibility = View.VISIBLE
                val style = NativeTemplateStyle.Builder()
                    .withMainBackgroundColor(ColorDrawable(backgroundColor.toArgb()))
                    .build()

                val templateView = view as TemplateView
                templateView.setStyles(style)
                templateView.setNativeAd(nativeAd)
            } else {
                view.visibility = View.INVISIBLE
            }
        })
}