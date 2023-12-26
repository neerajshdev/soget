package com.gd.reelssaver.ui.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.operator.map
import com.gd.reelssaver.ui.composables.TabItem
import com.gd.reelssaver.ui.screens.browser.BrowserComponent
import com.gd.reelssaver.ui.screens.browser.tab.ChildComp
import com.gd.reelssaver.ui.screens.browser.tab.TabComponent

@Composable
fun BrowserTabChooser(component: BrowserComponent) {
    val selectedTab by component.childTabs.map { it.active }.subscribeAsState()
    val tabComponents by component.tabComponents.subscribeAsState()

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Text(
                text = "${tabComponents.size} Tabs",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(
                        Alignment.CenterStart
                    )
                    .padding(start = 12.dp)
            )

            TextButton(
                onClick = { /* Todo:  Clear all event */ },
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
            for (tab in tabComponents) {
                val containerColor =
                    if (tab == selectedTab) colorScheme.surfaceContainerLow else colorScheme.surfaceContainerLowest

                Surface(color = containerColor) {
                    BrowserTabItem(comp = tab, onRemoveTab = {
                        // Todo: Handle remove child browser tab item
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabControl(
            onBackClick = { },
            onForwardClick = { },
            onAddTabClick = { },
        )
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


@Composable
private fun BrowserTabItem(comp: TabComponent, onRemoveTab: () -> Unit) {
    val childComp by comp.child.map { it.active.instance }.subscribeAsState()

    when (childComp) {
        is ChildComp.Webpage -> {
            val model by (childComp as ChildComp.Webpage).webpage.model.subscribeAsState()
            TabItem(title = model.pageTitle, url = model.pageUrl, onRemoveTab = onRemoveTab, isSelected = false)
        }

        is ChildComp.Homepage -> {
            val homepage = (childComp as ChildComp.Homepage).homePage
            TabItem(title = "Homepage", url = "@homepage", isSelected = false, onRemoveTab = onRemoveTab)
        }
    }
}