package com.gd.reelssaver.ui.contents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.ui.composables.TabControl
import com.gd.reelssaver.ui.blocs.TabChooserComponent
import com.gd.reelssaver.ui.blocs.TabChooserComponent.Event

@Composable
fun TabChooserContent(component: TabChooserComponent) {
    val currentTab by component.activeTab.collectAsState()
    val tabs by component.tabs.subscribeAsState()

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

            TextButton(
                onClick = { component.onEvent(Event.ClearAllTabs) },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
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
                    if (tab == currentTab) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerLowest
                Surface(color = containerColor) {
                    key(tab.id) {
                        TabItem(
                            title = component.views[tab.id]?.title ?: "Unknown",
                            url = tab.url,
                            onRemoveTab = { component.onEvent(Event.RemoveTab(tab)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(interactionSource = remember {
                                    MutableInteractionSource()
                                }, indication = null, onClick = {
                                    component.onEvent(Event.SelectTab(tab))
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
            onBackClick = { component.onEvent(Event.BackClick) },
            onForwardClick = { component.onEvent(Event.ForwardClick) },
            onAddTabClick = { component.onEvent(Event.AddNewTab) },
        )
    }
}

@Composable
fun TabItem(
    modifier: Modifier,
    title: String,
    url: String,
    onRemoveTab: () -> Unit
) {
    Row(modifier) {
        Box(
            modifier = Modifier
                .padding(end = 10.dp, start = 12.dp)
                .width(80.dp)
                .height(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
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
                color = MaterialTheme.colorScheme.onSurface,
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
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "RemoveTab",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}