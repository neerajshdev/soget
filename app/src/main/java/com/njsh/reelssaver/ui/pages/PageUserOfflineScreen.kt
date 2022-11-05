package com.njsh.reelssaver.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.njsh.reelssaver.R
import com.njsh.reelssaver.navigation.Page

class PageUserOfflineScreen() : Page()
{
    init
    {
        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cloud_offline),
                        tint = MaterialTheme.colors.secondary,
                        contentDescription = null
                    )
                    Text(
                        text = "YOU ARE OFFLINE",
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
