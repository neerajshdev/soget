package com.njsh.instadl.ui.components

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.nativead.NativeAd
import com.njsh.instadl.R
import com.njsh.instadl.ads.NativeAdLoader
import com.njsh.instadl.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LeftCurvedButton(
    modifier: Modifier = Modifier, painter: Painter, label: String, onClick: () -> Unit
) {
    val colors = MaterialTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .clip(
                shape = RoundedCornerShape(
                    topStart = 100.dp, bottomStart = 100.dp
                )
            )
            .clickable(onClick = onClick)
            .background(color = colors.primary)
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Icon(
            painter = painter, contentDescription = null, tint = colors.onPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = colors.onPrimary)
    }
}

@Preview
@Composable
fun PrevLeftCurvedButton() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth()) {
                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_instagram),
                    label = "INSTAGRAM",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {})
            }
        }
    }
}


@Composable
fun RightCurvedHeading(modifier: Modifier = Modifier, label: String) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
            .background(color = MaterialTheme.colors.primary)
            .padding(vertical = 16.dp, horizontal = 24.dp), contentAlignment = Alignment.CenterEnd
    ) {
        Text(text = label, color = MaterialTheme.colors.onPrimary)
    }
}


@Preview
@Composable
fun PrevRightCurvedHeading() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            RightCurvedHeading(modifier = Modifier.padding(16.dp), "ALL VIDEO DOWNLOADER")
        }
    }
}


@Composable
fun CircularProgressBar(isLoading: State<Boolean>) {
    if (isLoading.value) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    }
}


@Composable
fun NativeAdView(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colors
    var nativeAd by remember { mutableStateOf<NativeAd?>(null)}

    AndroidView(
        modifier = modifier,
        factory = {
        val view = LayoutInflater.from(it).inflate(R.layout.native_ad_view, null) as TemplateView
        view.apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
                  },
        update = { view->
            view.apply {
                val style: NativeTemplateStyle =
                    NativeTemplateStyle.Builder().withMainBackgroundColor(
                        ColorDrawable(colors.surface.toArgb())
                    ).build()
                if (nativeAd != null) {
                    setNativeAd(nativeAd)
                    visibility = View.VISIBLE
                } else {
                    visibility = View.INVISIBLE
                }
                setStyles(style)
            }
        }
    )

    val scope = rememberCoroutineScope()
    DisposableEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            nativeAd = NativeAdLoader.takeAndLoad()
        }

        onDispose {
            nativeAd?.let {
                it.destroy()
                nativeAd = null
            }
        }
    }
}
