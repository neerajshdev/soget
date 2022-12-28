package com.njsh.reelssaver.layer.ui.components

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ads.NativeAdLoader
import kotlinx.coroutines.launch

@Composable
fun Advertisement(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        if (maxHeight >= 350.dp) {
            MediumNativeAd()
        } else {
            SmallNativeAd()
        }
    }
}


@Composable
fun MediumNativeAd() {
    val scope = rememberCoroutineScope()
    val scheme = MaterialTheme.colorScheme
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    DisposableEffect(Unit) {
        scope.launch {
            nativeAd = NativeAdLoader.takeAndLoad()
        }
        onDispose {
            nativeAd?.destroy()
        }
    }

    if (nativeAd != null) println("native ad is available to show")
    AnimatedVisibility(
        visible = nativeAd != null,
    ) {
        AndroidView(factory = {
            (LayoutInflater.from(it)
                .inflate(R.layout.m_native_ad_view, null) as TemplateView).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                val style: NativeTemplateStyle =
                    NativeTemplateStyle.Builder().withMainBackgroundColor(
                        ColorDrawable(scheme.surface.toArgb())
                    ).build()
                setStyles(style)
                setNativeAd(nativeAd)
            }
        })
    }
}

@Composable
fun SmallNativeAd() {
    val scope = rememberCoroutineScope()
    val scheme = MaterialTheme.colorScheme
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    DisposableEffect(Unit) {
        scope.launch {
            nativeAd = NativeAdLoader.takeAndLoad()
        }
        onDispose {
            nativeAd?.destroy()
        }
    }

    AnimatedVisibility(
        visible = nativeAd != null,
    ) {
        AndroidView(factory = {
            (LayoutInflater.from(it).inflate(R.layout.native_ad_view, null) as TemplateView).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                val style: NativeTemplateStyle =
                    NativeTemplateStyle.Builder().withMainBackgroundColor(
                        ColorDrawable(scheme.surface.toArgb())
                    ).build()
                setStyles(style)
                setNativeAd(nativeAd)
            }
        })
    }
}
