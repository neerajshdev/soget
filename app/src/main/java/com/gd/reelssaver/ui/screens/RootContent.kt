package com.gd.reelssaver.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.ui.screens.browser.BrowserContent
import com.gd.reelssaver.ui.screens.downloads.DownloadContent
import com.gd.reelssaver.ui.screens.splash.SplashContent
import com.gd.reelssaver.ui.theme.AppTheme

@Preview
@Composable
private fun NavigationBarPreview() {
    val navigationBarItem = remember {
        listOf(
            NavigationBarItem(
                label = "Tabs",
                iconContent = {
                    Icon(imageVector = Icons.Rounded.TableRows, contentDescription = null)
                }, onSelect = {

                }),

            NavigationBarItem(
                label = "Downloads",
                iconContent = {
                    Icon(imageVector = Icons.Rounded.Download, contentDescription = null)
                }, onSelect = {})
        )
    }

    AppTheme {
        BottomNavigation(
            items = navigationBarItem,
            selectedItem = navigationBarItem.first(),
        )
    }
}

@Composable
fun RootContent(component: RootComponent) {
    val childStack by component.childStack.subscribeAsState()
    val navBarItems = listOf(
        NavigationBarItem(
            label = "Tabs",
            iconContent = {
                Icon(imageVector = Icons.Rounded.TableRows, contentDescription = null)
            },
            onSelect = {
                component.onEvent(Event.OnTabMenuSelect)
            }
        ),
        NavigationBarItem(
            label = "Downloads",
            iconContent = {
                Icon(imageVector = Icons.Rounded.Download, contentDescription = null)
            },
            onSelect = {
                component.onEvent(Event.OnDownloadMenuSelect)
            }
        )
    )

    val selectedMenuItem = when (childStack.active.instance) {
        is Child.Browser -> navBarItems[0]
        else -> navBarItems[1]
    }

    val columnModifier = Modifier.fillMaxSize()

    Children(stack = childStack) {
        when (val active = it.instance) {
            is Child.Splash -> {
                SplashContent(active.component, modifier = Modifier.fillMaxSize())
            }

            is Child.Browser -> BrowserContent(
                active.component,
                bottomNavBar = {
                    BottomNavigation(
                        items = navBarItems,
                        selectedItem = selectedMenuItem,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            is Child.Downloads -> Column(modifier = columnModifier) {
                DownloadContent(
                    component = active.component, modifier = Modifier
                        .weight(1f)
                        .statusBarsPadding(),
                    bottomNavBar = {
                        BottomNavigation(
                            items = navBarItems,
                            selectedItem = selectedMenuItem,
                        )
                    }
                )
            }
        }

    }
}

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    items: List<NavigationBarItem>,
    selectedItem: NavigationBarItem,
) {
    NavigationBar(modifier, contentColor = colorScheme.surfaceContainer) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item == selectedItem,
                onClick = item.onSelect,
                icon = item.iconContent,
                label = {
                    Text(
                        text = item.label,
                        style = typography.titleMedium,
                        color = LocalContentColor.current
                    )
                }
            )
        }
    }
}


data class NavigationBarItem(
    val label: String,
    val iconContent: @Composable () -> Unit,
    val onSelect: () -> Unit
)