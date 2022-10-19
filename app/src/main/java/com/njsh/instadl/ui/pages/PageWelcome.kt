package com.njsh.instadl.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.njsh.instadl.App
import com.njsh.instadl.R
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.LeftCurvedButton
import com.njsh.instadl.ui.components.RightCurvedHeading

class PageWelcome(val onNavigateTo: (String) -> Unit) : Page()
{
    init
    {
        addContent {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                Content()
            }
        }
    }


    @Composable
    private fun Content()
    {
        Column(
            Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                RightCurvedHeading(label = "ALL VIDEO DOWNLOADER")
            }
            OptionsLayout(modifier = Modifier.fillMaxSize())
        }
    }

    @Composable
    private fun OptionsLayout(modifier: Modifier = Modifier)
    {
        Box(contentAlignment = Alignment.CenterEnd, modifier = modifier) {
            Column {
                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_right_to_bracket_solid),
                    label = "ENTER",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = { onNavigateTo(Route.MainScreen.name) })

                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_share_nodes_solid),
                    label = "SHARE THIS APP",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = { App.toast("This feature not added yet") })

                LeftCurvedButton(painter = painterResource(id = R.drawable.ic_star_solid),
                    label = "RATE THIS APP",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = { App.toast("This feature not added yet") })
            }
        }
    }
}