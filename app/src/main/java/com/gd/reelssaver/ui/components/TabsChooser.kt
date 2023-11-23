package com.gd.reelssaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.ui.state.PageType
import com.gd.reelssaver.ui.state.Tab
import com.gd.reelssaver.ui.theme.AppTheme


@Preview
@Composable
fun TabsChooserPrev() {
    AppTheme {
        Surface(color = colorScheme.surfaceContainerLowest) {
            val tabs = listOf(Tab(), Tab(pageType = PageType.WEBPAGE, url = "https://example.com/"))

            TabsChooser(
                tabs = tabs,
                selectedTab = tabs.first(),
                onRemoveTab = {},
                onClearAllTabs = {},
                onAddTabClick = {},
                onBackClick = {},
                onForwardClick = {},
                onTabSelect = {}
            )
        }
    }
}

@Composable
fun TabsChooser(
    selectedTab: Tab,
    tabs: List<Tab>,
    onRemoveTab: (Tab) -> Unit,
    onClearAllTabs: () -> Unit,
    onAddTabClick: () -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onTabSelect: (Tab) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Text(
                text = "${tabs.size} Tabs",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(
                        Alignment.CenterStart
                    )
                    .padding(start = 12.dp)
            )

            TextButton(onClick = onClearAllTabs, modifier = Modifier.align(Alignment.TopEnd)) {
                Text(text = "Clear all")
            }
        }

        Column(
            modifier = Modifier
                .heightIn(max = 400.dp)
                .verticalScroll(rememberScrollState())
        ) {
            for (tab in tabs) {
                val containerColor =
                    if (tab == selectedTab) colorScheme.surfaceContainerLow else colorScheme.surfaceContainerLowest
                Surface(color = containerColor) {
                    key(tab.id){
                        TabItem(
                            tab = tab,
                            onRemoveTab = { onRemoveTab(tab) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(interactionSource = remember {
                                    MutableInteractionSource()
                                }, indication = null, onClick = {
                                    onTabSelect(tab)
                                })
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabControl(
            onBackClick = onBackClick,
            onForwardClick = onForwardClick,
            onAddTabClick = onAddTabClick,
        )
    }
}

@Composable
fun TabItem(tab: Tab, modifier: Modifier, onRemoveTab: () -> Unit) {
    val title = when (tab.pageType) {
        PageType.HOMEPAGE -> "Homepage"
        PageType.WEBPAGE -> tab.webView?.title ?: "Unknown"
    }
    val url = when (tab.pageType) {
        PageType.HOMEPAGE -> "about:blank"
        PageType.WEBPAGE -> tab.webView?.url ?: "Unknown"
    }


    Row(modifier) {
        Box(
            modifier = Modifier
                .padding(end = 10.dp, start = 12.dp)
                .width(80.dp)
                .height(56.dp)
                .background(
                    color = colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.Public,
                contentDescription = null,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onRemoveTab) {
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "RemoveTab", tint = colorScheme.outline)
        }
    }
}

@Composable
fun TabControl(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onAddTabClick: () -> Unit
) {

    val btnColor = colorScheme.surfaceContainerLow
    val contentColor = colorScheme.onSurface

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = btnColor,
                contentColor = contentColor
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "add new tab"
            )
        }

        Button(
            onClick = onForwardClick, colors = ButtonDefaults.buttonColors(
                containerColor = btnColor,
                contentColor = contentColor
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "add new tab"
            )
        }

        Button(
            onClick = onAddTabClick, colors = ButtonDefaults.buttonColors(
                containerColor = btnColor,
                contentColor = contentColor
            )
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "add new tab")
        }
    }
}