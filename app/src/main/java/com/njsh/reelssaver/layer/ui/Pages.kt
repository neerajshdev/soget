package com.njsh.reelssaver.layer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.R
import kotlinx.coroutines.delay


object RouteName {
    val HOME = "HOME"
    val INSTAGRAM = "INSTAGRAM"
    val FACEBOOK = "FACEBOOK"
    val SHORT_VIDEOS = "SHORT VIDEOS"
}

@Composable
fun PageHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var splash by rememberSaveable { mutableStateOf(false) }

    if (splash) {
        Splash(modifier = modifier) { splash = false }
    } else {
        NavHost(
            navController = navController, startDestination = RouteName.HOME, modifier = modifier
        ) {
            composable(RouteName.HOME) {
                Home()
            }
            composable(RouteName.INSTAGRAM) {

            }

            composable(RouteName.FACEBOOK) {

            }
            composable(RouteName.SHORT_VIDEOS) {

            }
        }
    }
}


@Composable
fun Splash(modifier: Modifier = Modifier, splashTime: Long = 4000, onSplashEnd: () -> Unit) {
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
            delay(splashTime)
            onSplashEnd()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    @Composable
    fun OptionButtons() {
        val commonModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .padding(2.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))

        Column {
            BigButtonLayer(
                icon = painterResource(id = R.drawable.ic_outlined_instagram),
                text = "Instagram",
                desText = "Paste link and download instagram short video",
                modifier = commonModifier
            )

            BigButtonLayer(
                icon = painterResource(id = R.drawable.ic_outlined_facebook),
                text = "Facebook",
                desText = "Paste link and download facebook videos",
                modifier = commonModifier
            )

            BigButtonLayer(
                icon = painterResource(id = R.drawable.ic_outlined_video_clip),
                text = "Short statuses",
                desText = "Watch & enjoy! short status videos",
                modifier = commonModifier
            )
        }
    }

    @Composable
    fun Advertisement() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.BottomCenter)
        ) { // TODO: REPLACE WITH NATIVE AD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(bottom = 16.dp)
                    .background(color = Color.Cyan)
            )
        }
    }

    Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)){
        TopAppBar(title = { Text(text = "Instagram") }, navigationIcon = {
            Icon(Icons.Default.Menu, contentDescription = null)
        })
        OptionButtons()
        Advertisement()
    }
}


@Preview
@Composable
fun PHome() {
    Home()
}


@Composable
fun InstagramDownload() {

}