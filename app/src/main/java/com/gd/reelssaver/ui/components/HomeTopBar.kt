package com.gd.reelssaver.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gd.reelssaver.R
import com.gd.reelssaver.ui.screens.PageCountIcon
import com.gd.reelssaver.ui.theme.AppTheme
import com.gd.reelssaver.ui.theme.useDarkTheme
import kotlinx.coroutines.launch

@Preview
@Composable
fun HomeTopBarPrev() {
    AppTheme {
        HomeTopBar(tabsCount = 2)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    tabsCount: Int,
    onOpenTabs: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        modifier = modifier,
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = onOpenTabs) {
                PageCountIcon(count = tabsCount)
            }

//            IconButton(onClick = {}) {
//                Icon(imageVector = Icons.Default.Info, contentDescription = "How to use")
//            }

            IconButton(onClick = {
                scope.launch {
                    useDarkTheme = useDarkTheme.not()
                }
            }) {
                Icon(
                    painter = painterResource(id = if (useDarkTheme) R.drawable.baseline_dark_mode_24 else R.drawable.baseline_light_mode_24),
                    contentDescription = "Change Theme",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}