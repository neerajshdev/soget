package com.gd.reelssaver.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


data class NavBarItem(
    val name: String,
    val iconRes: Int,
    val onItemSelect: () -> Unit
)

@Composable
fun BottomNavigationBar(
    vararg navBarItem: NavBarItem,
    modifier: Modifier = Modifier
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val colorScheme = MaterialTheme.colorScheme


    NavigationBar(modifier) {
        navBarItem.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItemIndex,
                onClick = {
                    item.onItemSelect()
                    selectedItemIndex = index
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (index == selectedItemIndex) colorScheme.surfaceTint else colorScheme.outline
                    )
                },
                label = { Text(text = item.name) }
            )
        }
    }
}