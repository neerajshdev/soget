package com.gd.reelssaver.ui.components

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.NativeAdLoader
import com.gd.reelssaver.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Preview
@Composable
fun BannerSmallNativeAdPreview() {
    AppTheme {
        BannerSmallNativeAd()
    }
}

@Composable
fun BannerSmallNativeAd(
    modifier: Modifier = Modifier,
    refreshTimeSec: Int = 60
) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val backgroundColor = MaterialTheme.colorScheme.background

    nativeAd?.let {
        DisposableEffect(it) {
            onDispose {
                it.destroy()
            }
        }
    }

    if (!LocalInspectionMode.current) LaunchedEffect(Unit) {
        val time = refreshTimeSec * 1000L
        while (isActive) {
            nativeAd = NativeAdLoader.takeAndLoad()
            delay(time)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            LayoutInflater.from(it).inflate(R.layout.small_native_ad_view, null).apply {
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
        }
    )
}