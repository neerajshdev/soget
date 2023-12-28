package com.gd.reelssaver.ui.screens.browser

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.operator.map
import com.gd.reelssaver.ui.composables.TabItem
import com.gd.reelssaver.ui.screens.browser.tab.ChildComp
import com.gd.reelssaver.ui.screens.browser.tab.TabComponent
import com.gd.reelssaver.ui.screens.browser.tab.TabContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserContent(comp: BrowserComponent, modifier: Modifier = Modifier) {
    val tabs by comp.childTabs.subscribeAsState()
    val isTabChooserOpen by comp.isTabChooserOpen.subscribeAsState()

    Crossfade(
        targetState = tabs.active,
        label = "TabsCrossFade",
        modifier = modifier
    ) { tab ->
        TabContent(tab.instance)
    }


    if (isTabChooserOpen) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { comp.onEvent(Event.OnTabChooserDissmised) },
            containerColor = colorScheme.surfaceContainerLowest,
            sheetState = sheetState
        ) {

            BrowserTabChooser(component = comp)
        }

        LaunchedEffect(key1 = sheetState) {
            sheetState.show()
        }
    }
}


/**
 *  TabChooser content that gives control to remove and add and select
 *  Child tab components.
 */
@Composable
private fun BrowserTabChooser(component: BrowserComponent) {
    val selectedTab by component.childTabs.map { it.active.instance }.subscribeAsState()
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
                onClick = {
                    component.onEvent(Event.ClearAllTab)
                },
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
            // list of tab items
            for ((index, tab) in tabComponents.withIndex()) {
                val isSelected = tab == selectedTab
                val containerColor =
                    if (isSelected) colorScheme.surfaceContainerLow else colorScheme.surfaceContainerLowest


                Surface(color = containerColor) {
                    BrowserTabItem(
                        comp = tab,
                        isSelected = isSelected,
                        onRemoveTab = {
                            component.onEvent(Event.RemoveTab(index))
                        },
                        modifier = Modifier
                            .clickable {
                                if (!isSelected) {
                                    component.onEvent(Event.SelectTab(index))
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabControl(
            onBackClick = {
                // todo: implement back click from tab chooser
            },
            onForwardClick = {
                // todo: implement forward click from tab chooser
            },
            onAddTabClick = { component.onEvent(Event.AddNewTab(TabPage.Homepage)) },
        )
    }
}


@Composable
private fun TabControl(
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
private fun BrowserTabItem(
    modifier: Modifier = Modifier,
    comp: TabComponent,
    isSelected: Boolean,
    onRemoveTab: () -> Unit
) {
    val childComp by comp.child.map { it.active.instance }.subscribeAsState()
    val title = when (childComp) {
        is ChildComp.Webpage -> {
            (childComp as ChildComp.Webpage).webpage.model.map { it.pageTitle }
                .subscribeAsState().value
        }

        is ChildComp.Homepage -> {
            "Homepage"
        }
    }

    val pageUrl = when (childComp) {
        is ChildComp.Webpage -> {
            (childComp as ChildComp.Webpage).webpage.model.map { it.pageUrl }
                .subscribeAsState().value
        }

        is ChildComp.Homepage -> {
            "@homepage"
        }
    }

    TabItem(
        title = title,
        url = pageUrl,
        onRemoveTab = onRemoveTab,
        isSelected = isSelected,
        modifier = modifier
    )
}