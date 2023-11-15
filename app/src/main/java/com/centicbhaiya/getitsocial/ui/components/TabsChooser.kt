package com.centicbhaiya.getitsocial.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.centicbhaiya.getitsocial.ui.state.PageType
import com.centicbhaiya.getitsocial.ui.state.Tab
import com.centicbhaiya.getitsocial.ui.theme.AppTheme


@Preview
@Composable
fun TabsChooserPrev() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
            TabsChooser(
                tabs = listOf(Tab(), Tab(PageType.WEBPAGE, "https://example.com/")),
                onRemoveTab = {}, onClearAllTabs = {}
            )
        }
    }
}

@Composable
fun TabsChooser(tabs: List<Tab>, onRemoveTab: (Tab) -> Unit, onClearAllTabs: () -> Unit) {
    Box {
        Column(modifier = Modifier.heightIn(min = 400.dp)) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
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

            tabs.forEach {
                Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                    TabItem(
                        tab = it,
                        onRemoveTab = { onRemoveTab(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }


    }
}

@Composable
fun TabItem(tab: Tab, modifier: Modifier, onRemoveTab: () -> Unit) {
    val title = when (tab.pageType) {
        PageType.HOMEPAGE -> "Homepage"
        PageType.WEBPAGE -> "title"
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
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
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

        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
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
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "RemoveTab")
        }
    }
}