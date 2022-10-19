package com.njsh.instadl.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.njsh.instadl.R
import com.njsh.instadl.navigation.Page
import kotlinx.coroutines.delay

class PageSplash(onNavigateTo: (String) -> Unit) : Page()
{
    init
    {
        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.splash_image),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            LaunchedEffect(key1 = Unit) {
                delay(5000)
                onNavigateTo(Route.WelcomeScreen.name)
            }
        }
    }
}