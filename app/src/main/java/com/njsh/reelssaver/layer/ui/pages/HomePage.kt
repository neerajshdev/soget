package com.njsh.reelssaver.layer.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.R
import com.njsh.reelssaver.ads.checkAndShowAd
import com.njsh.reelssaver.layer.ui.components.Advertisement
import com.njsh.reelssaver.layer.ui.components.BigButtonLayer
import com.njsh.reelssaver.layer.ui.theme.AppTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    @Composable
    fun OptionButtons(modifier: Modifier = Modifier) {
        val commonModifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .padding(2.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier) {
            BigButtonLayer(icon = painterResource(id = R.drawable.ic_outlined_instagram),
                text = "Instagram",
                desText = "Paste link and download instagram short video",
                modifier = commonModifier,
                onClick = {
                    scope.launch {
                        checkAndShowAd(context) {
                            navController.navigate(RouteName.INSTAGRAM)
                        }
                    }
                }
            )

            BigButtonLayer(icon = painterResource(id = R.drawable.ic_outlined_facebook),
                text = "Facebook",
                desText = "Paste link and download facebook videos",
                modifier = commonModifier,
                onClick = {
                    scope.launch{
                        checkAndShowAd(context) {
                            navController.navigate(RouteName.FACEBOOK)
                        }
                    }
                }
            )

            BigButtonLayer(icon = painterResource(id = R.drawable.ic_outlined_video_clip),
                text = "Short statuses",
                desText = "Watch & enjoy! short status videos",
                modifier = commonModifier,
                onClick = {
                    scope.launch {
                        checkAndShowAd(context) {
                            navController.navigate(RouteName.SHORT_VIDEOS)
                        }
                    }
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val onBackground = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f)

        CenterAlignedTopAppBar(title = {
            Text(
                text = "Home", color = onBackground
            )
        })

        OptionButtons(
            modifier = Modifier.wrapContentSize(Alignment.TopStart)
        )

        Advertisement()
    }
}

@Preview
@Composable
private fun PHome() {
    AppTheme {
        Home(rememberNavController())
    }
}


