package com.gd.reelssaver.ui.screens.browser.tab.pages.homepage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.R
import com.gd.reelssaver.ads.InterstitialAdManager
import com.gd.reelssaver.ui.composables.HomeTopBar
import com.gd.reelssaver.ui.composables.InputUrlFieldCard
import com.gd.reelssaver.ui.composables.MediumSizeNativeAd
import com.gd.reelssaver.ui.theme.AppTheme
import java.net.URL

@Preview(
    wallpaper = Wallpapers.NONE, device = "spec:width=411dp,height=891dp", showSystemUi = true,
    showBackground = true
)
@Composable
private fun HomepageContentPreview() {
    val component = remember { FakeHomePage() }
    AppTheme {
        HomepageContent(component = component) {}
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomepageContent(
    modifier: Modifier = Modifier,
    component: HomePageComponent,
    bottomNavBar: @Composable () -> Unit
) {
    val useDarkTheme by component.isDarkTheme.subscribeAsState()
    LocalContext.current
    val isKeyboardOpen = WindowInsets.isImeVisible
    val focusManager = LocalFocusManager.current

    val inputText by component.inputText.subscribeAsState()
    val pageCount by component.tabsCount.subscribeAsState()

    val openInputText: () -> Unit = {
        try {
            val url = URL(inputText)
            component.onEvent(Event.OnOpenWebSite(url))
        } catch (ex: Exception) {
            component.onEvent(Event.SearchWeb(inputText))
        }
    }

    val updateInputText: (String) -> Unit = {
        component.onEvent(Event.UpdateInputText(it))
    }

    LaunchedEffect(key1 = isKeyboardOpen, block = {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    })

    Scaffold(
        modifier = modifier,
        bottomBar = bottomNavBar,
        topBar = {
            HomeTopBar(
                tabsCount = pageCount,
                useDarkTheme = useDarkTheme,
                onOpenTabs = { component.onEvent(Event.OpenTabChooser) },
                onToggleTheme = { component.onEvent(Event.ToggleTheme) }
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                // Hero section with app logo and tagline
                HeroSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Search Input with improved design
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(500)
                    )
                ) {
                    InputUrlFieldCard(
                        url = inputText,
                        onValueChange = updateInputText,
                        onKeyBoardAction = openInputText,
                        onGoActionClick = openInputText,
                        onContentPaste = updateInputText,
                        modifier = Modifier
                            .layoutId("url_edit_text")
                            .fillMaxWidth()
                    )
                }

                // Popular Platforms section
                PopularPlatformsSection(
                    onSiteOpen = { siteUrl ->
                        component.onEvent(Event.OnOpenWebSite(siteUrl))
                        InterstitialAdManager.tryAd()
                    },
                    modifier = Modifier
                        .layoutId("social_sites")
                        .fillMaxWidth()
                )

                // Ad placement with better framing
                ElevatedCard(
                    modifier = Modifier
                        .layoutId("ad_place_holder")
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = colorScheme.surface
                    )
                ) {
                    MediumSizeNativeAd(
                        refreshTimeSec = 80,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ad_placeholder),
                                contentDescription = "Ad Placeholder",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .fillMaxSize()
                            )
                            
                            // Overlay text "Advertisement"
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Advertisement",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun HeroSection(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(colorScheme.primaryContainer.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Social Media Browser",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = colorScheme.primary
        )
        
        Text(
            text = "Download content from your favorite platforms",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PopularPlatformsSection(
    modifier: Modifier = Modifier,
    onSiteOpen: (URL) -> Unit
) {
    Column(modifier = modifier) {
        SectionTitle(title = "Popular Platforms")
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                PlatformButton(
                    name = "Facebook",
                    icon = R.drawable.facebook,
                    onClick = { onSiteOpen(URL("https://www.facebook.com/")) }
                )
                
                PlatformButton(
                    name = "Instagram",
                    icon = R.drawable.instagram,
                    onClick = { onSiteOpen(URL("https://www.instagram.com/")) }
                )
                
                PlatformButton(
                    name = "YouTube",
                    icon = R.drawable.youtube,
                    onClick = { onSiteOpen(URL("https://www.youtube.com/")) }
                )
                
                PlatformButton(
                    name = "Google",
                    icon = R.drawable.google,
                    onClick = { onSiteOpen(URL("https://www.google.com/")) }
                )
            }
        }
    }
}

@Composable
private fun PlatformButton(
    name: String,
    icon: Int,
    onClick: () -> Unit,
    iconSize: Dp = 32.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(colorScheme.surface)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = name,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold
        ),
        color = colorScheme.onBackground,
        modifier = Modifier
            .padding(start = 4.dp, bottom = 12.dp)
            .then(modifier)
    )
}
