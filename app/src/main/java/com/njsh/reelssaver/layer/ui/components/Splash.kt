package com.njsh.reelssaver.layer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.njsh.reelssaver.R
import com.njsh.reelssaver.layer.ui.UiState

@Composable
fun Splash(modifier: Modifier = Modifier, uiState: UiState,  onSplashEnd: () -> Unit) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.image_splash_circle),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
        )

        LaunchedEffect(key1 = Unit) {
            uiState.waitUntilOnline()
            uiState.syncFirebase()
            onSplashEnd()
        }
    }
}