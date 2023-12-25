package com.gd.reelssaver.ui.contents

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.ui.blocs.TabChooserComponent
import com.gd.reelssaver.ui.blocs.TabChooserComponent.Event

@Composable
fun TabChooserContent(component: TabChooserComponent) {
    val currentTab by component.selectedPage.collectAsState()
    val pages by component.pages.collectAsState(initial = emptyList())
    val selectedPage by component.selectedPage.collectAsState()

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Text(
                text = "${pages.size} Tabs",
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
            for (page in pages) {
                val containerColor =
                    if (page == currentTab)
                        colorScheme.surfaceContainerLow
                    else colorScheme.surfaceContainerLowest

                Surface(color = containerColor) {
                    key(page.id) {
                        TabItem(
                            title = selectedPage?.view?.value?.title ?: "Unknown",
                            url = selectedPage?.currentUrl?.value ?: "Unknown",
                            onRemoveTab = {
                                component.onEvent(
                                    Event.RemovePage(
                                        selectedPage?.id ?: ""
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(interactionSource = remember {
                                    MutableInteractionSource()
                                }, indication = null) {
                                    component.onEvent(Event.SelectPage(selectedPage?.id ?: ""))
                                }
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
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "RemoveTab",
                tint = colorScheme.outline
            )
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