package com.gd.reelssaver.ui.screens.browser

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.gd.reelssaver.ui.router.ChildTabs
import com.gd.reelssaver.ui.screens.browser.tab.ChildComp
import com.gd.reelssaver.ui.screens.browser.tab.Config
import com.gd.reelssaver.ui.screens.browser.tab.TabComponent
import com.gd.reelssaver.ui.screens.browser.tab.TabContent
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.FakeHomePage
import com.gd.reelssaver.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserContent(
    comp: BrowserComponent,
    modifier: Modifier = Modifier,
    bottomNavBar: @Composable () -> Unit,
) {
    val tabs by comp.childTabs.subscribeAsState()
    val isTabChooserOpen by comp.isTabChooserOpen.subscribeAsState()

    Surface(
        color = colorScheme.background,
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = tabs.active,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            modifier = Modifier.fillMaxSize(),
            label = "TabsTransition"
        ) { tab ->
            TabContent(tab.instance, Modifier.fillMaxSize(), bottomNavBar = bottomNavBar)
        }
    }

    if (isTabChooserOpen) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = { comp.onEvent(Event.OnTabChooserDissmised) },
            containerColor = colorScheme.surfaceContainerLow,
            sheetState = sheetState,
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(width = 40.dp, height = 4.dp),
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(2.dp)
                    ) {}
                }
            }
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

    Column(
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        // Header with actions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Browser Tabs",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            if (tabComponents.size > 1) {
                TextButton(
                    onClick = { component.onEvent(Event.ClearAllTab) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorScheme.error
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear all tabs",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "Close All",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        
        Text(
            text = "${tabComponents.size} ${if (tabComponents.size == 1) "Tab" else "Tabs"} Open",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Tabs section
        if (tabComponents.isEmpty()) {
            EmptyTabsState(
                onAddNewTab = { component.onEvent(Event.AddNewTab(TabPage.Homepage)) }
            )
        } else {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // list of tab items
                for ((index, tab) in tabComponents.withIndex()) {
                    val isSelected = tab == selectedTab
                    TabCard(
                        tab = tab,
                        isSelected = isSelected,
                        onTabClick = { 
                            if (!isSelected) {
                                component.onEvent(Event.SelectTab(index))
                            }
                        },
                        onRemoveTab = { component.onEvent(Event.RemoveTab(index)) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab control panel
        TabControlPanel(
            onBackClick = { /* Implement back click */ },
            onForwardClick = { /* Implement forward click */ },
            onAddTabClick = { component.onEvent(Event.AddNewTab(TabPage.Homepage)) },
        )
    }
}

@Composable
private fun EmptyTabsState(onAddNewTab: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colorScheme.primary.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tabs open",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            FilledTonalButton(
                onClick = onAddNewTab,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorScheme.primaryContainer
                )
            ) {
                Text("Open New Tab")
            }
        }
    }
}

@Composable
private fun TabCard(
    tab: TabComponent,
    isSelected: Boolean,
    onTabClick: () -> Unit,
    onRemoveTab: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onTabClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                colorScheme.primaryContainer.copy(alpha = 0.7f)
            else 
                colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab content (title and URL)
            TabContent(
                tab = tab, 
                isSelected = isSelected,
                modifier = Modifier.weight(1f)
            )
            
            // Close button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isSelected)
                            colorScheme.primary.copy(alpha = 0.1f)
                        else
                            colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .clickable(onClick = onRemoveTab)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = "Close tab",
                    tint = if (isSelected)
                        colorScheme.primary
                    else
                        colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TabContent(
    tab: TabComponent,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val childComp by tab.child.map { it.active.instance }.subscribeAsState()
    val title = when (childComp) {
        is ChildComp.Webpage -> {
            (childComp as ChildComp.Webpage).webpage.model.map { it.pageTitle }
                .subscribeAsState().value
        }
        is ChildComp.Homepage -> "Homepage"
    }

    val pageUrl = when (childComp) {
        is ChildComp.Webpage -> {
            (childComp as ChildComp.Webpage).webpage.model.map { it.pageUrl }
                .subscribeAsState().value
        }
        is ChildComp.Homepage -> "@homepage"
    }

    Column(modifier = modifier) {
        Text(
            text = title.ifEmpty { "New Tab" },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected)
                colorScheme.onPrimaryContainer
            else
                colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = pageUrl,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected)
                colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            else
                colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TabControlPanel(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onAddTabClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            NavigationButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = "Back"
            )

            NavigationButton(
                onClick = onForwardClick,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                label = "Forward"
            )

            FilledTonalButton(
                onClick = onAddTabClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New Tab",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "New Tab",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun NavigationButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = colorScheme.primaryContainer.copy(alpha = 0.7f),
                contentColor = colorScheme.primary
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.primary.copy(alpha = 0.8f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
private fun BrowserTabChooserPreview() {
    AppTheme {
        Surface(
            color = colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            BrowserTabChooser(component = PreviewBrowserComponent())
        }
    }
}

private class PreviewBrowserComponent : BrowserComponent {

    override val isDarkTheme: Value<Boolean> = MutableValue(false)

    override val tabCount: Value<Int> = MutableValue(1)

    override val childTabs: Value<ChildTabs<com.gd.reelssaver.ui.screens.browser.Config, TabComponent>> =
        MutableValue(
            ChildTabs(
                active = Child.Created(
                    Config(
                        initialPage =
                        TabPage.Homepage
                    ),
                    createTabComponent()
                ),
                children = emptyList()
            )
        )

    private fun createTabComponent(): TabComponent {
        return object : TabComponent {
            override val child: Value<ChildStack<Config, ChildComp>> = MutableValue(
                ChildStack(
                    configuration = Config.HomePage,
                    instance = ChildComp.Homepage(FakeHomePage())
                )
            )
        }
    }


    // Create mock tabComponents
    override val tabComponents: Value<List<TabComponent>> =
        MutableValue(listOf(createTabComponent()))

    // Create mock isTabChooserOpen state
    override val isTabChooserOpen: Value<Boolean> = MutableValue(true)

    // Implement the required onEvent function
    override fun onEvent(event: Event) {
        // No-op for preview
    }
}