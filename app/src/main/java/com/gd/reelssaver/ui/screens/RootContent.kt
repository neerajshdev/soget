package com.gd.reelssaver.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.ui.composables.ExitDialogBottomSheet
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
                label = "Browser",
                iconContent = {
                    Icon(imageVector = Icons.Filled.TableRows, contentDescription = "Browser Tabs")
                }, onSelect = {

                }),

            NavigationBarItem(
                label = "Downloads",
                iconContent = {
                    Icon(imageVector = Icons.Filled.Download, contentDescription = "Downloads")
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
    val openExitDialog by component.openExitDialog.subscribeAsState()

    val navBarItems = listOf(
        NavigationBarItem(
            label = "Browser",
            iconContent = {
                Icon(
                    imageVector = Icons.Filled.TableRows, 
                    contentDescription = "Browser Tabs"
                )
            },
            onSelect = {
                component.onEvent(Event.OnTabMenuSelect)
            }
        ),
        NavigationBarItem(
            label = "Downloads",
            iconContent = {
                Icon(
                    imageVector = Icons.Filled.Download, 
                    contentDescription = "Downloads"
                )
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

    Children(stack = childStack) {
        when (val active = it.instance) {
            is Child.Splash -> {
                SplashContent(active.component, modifier = Modifier.fillMaxSize())
            }

            is Child.Browser -> {
                Surface(
                    color = colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    BrowserContent(
                        active.component,
                        bottomNavBar = {
                            BottomNavigation(
                                items = navBarItems,
                                selectedItem = selectedMenuItem,
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            is Child.Downloads -> {
                Surface(
                    color = colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    DownloadContent(
                        component = active.component, 
                        modifier = Modifier
                            .fillMaxSize()
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

    ExitDialogBottomSheet(
        enable = openExitDialog,
        onDismiss = {
            component.onEvent(Event.OnExitDialogDismiss)
        },
        onExitConfirm = {
            component.onEvent(Event.OnExitConfirm)
        },
        onExitCancel = {
            component.onEvent(Event.OnExitDialogDismiss)
        }
    )
}

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    items: List<NavigationBarItem>,
    selectedItem: NavigationBarItem,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp),
        color = colorScheme.surfaceContainerHigh,
        tonalElevation = 3.dp
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            containerColor = colorScheme.surfaceContainerHigh,
            contentColor = colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                val selected = item == selectedItem
                NavigationBarItem(
                    selected = selected,
                    onClick = item.onSelect,
                    icon = item.iconContent,
                    label = {
                        Text(
                            text = item.label,
                            style = typography.labelLarge.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selected) 
                                colorScheme.primary 
                            else 
                                colorScheme.onSurfaceVariant
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorScheme.primary,
                        unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        selectedTextColor = colorScheme.primary,
                        unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        indicatorColor = colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}


data class NavigationBarItem(
    val label: String,
    val iconContent: @Composable () -> Unit,
    val onSelect: () -> Unit
)