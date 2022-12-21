package com.njsh.reelssaver.layer.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.njsh.reelssaver.R
import com.njsh.reelssaver.layer.ui.components.BigButtonLayer
import com.njsh.reelssaver.layer.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    @Composable
    fun OptionButtons() {
        val commonModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 16.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .padding(2.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))

        Column {
            BigButtonLayer(
                icon = painterResource(id = R.drawable.ic_outlined_instagram),
                text = "Instagram",
                desText = "Paste link and download instagram short video",
                modifier = commonModifier,
                onClick = {}
            )

            BigButtonLayer(
                icon = painterResource(id = R.drawable.ic_outlined_facebook),
                text = "Facebook",
                desText = "Paste link and download facebook videos",
                modifier = commonModifier,
                onClick = {}
            )

            BigButtonLayer(
                icon = painterResource(id = R.drawable.ic_outlined_video_clip),
                text = "Short statuses",
                desText = "Watch & enjoy! short status videos",
                modifier = commonModifier,
                onClick = {}
            )
        }
    }

    @Composable
    fun ColumnScope.Advertisement() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentSize(align = Alignment.Center)
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

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp)
    ) {
        val onBackground = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.60f)

        TopAppBar(title = {
            Text(text = "Instagram", color = onBackground, modifier = Modifier.padding(start = 16.dp))
        }, navigationIcon = {
            Icon(Icons.Default.Menu, contentDescription = null, tint = onBackground)
        })
        OptionButtons()
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


