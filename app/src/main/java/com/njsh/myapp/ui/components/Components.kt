package com.njsh.myapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njsh.myapp.ui.theme.MyappTheme

class TopAppbar(var title: String = "", var menuIcon: ImageVector = Icons.Default.Menu) {
    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        TopAppBar(
            modifier = modifier.clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)),
            navigationIcon = { NavIcon() }, title = {Title()}, actions = {}
        )
    }

    @Composable
    private fun NavIcon()
    {
        IconButton(onClick = {})
        {
            Icon(imageVector = menuIcon, contentDescription = "menu icon")
        }
    }

    @Composable
    private fun Title()
    {
        Text(text = title)
    }
}


@Preview
@Composable
fun TopAppBarPrev() {
    MyappTheme {
        val appBar = TopAppbar().apply {
            title = "Sample"
        }
        appBar.Compose()
    }
}