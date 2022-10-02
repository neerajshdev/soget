package com.njsh.myapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njsh.myapp.ui.theme.MyappTheme

// this file contains common compose components

class TopAppbar(title: String = "", var menuIcon: ImageVector = Icons.Default.Menu)
{
    val title  = mutableStateOf(title)
    var onMenuClick: (() -> Unit)? = null

    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        TopAppBar(modifier = modifier.clip(
            RoundedCornerShape(
                bottomStart = 14.dp, bottomEnd = 14.dp
            )
        ), navigationIcon = { NavIcon() }, title = { Title() }, actions = {})
    }

    @Composable
    private fun NavIcon()
    {
        IconButton(onClick = { onMenuClick?.invoke() }) {
            Icon(imageVector = menuIcon, contentDescription = "menu icon")
        }
    }

    @Composable
    private fun Title()
    {
        Text(text = title.value)
    }
}


@Preview
@Composable
fun TopAppBarPrev()
{
    MyappTheme {
        val appBar = TopAppbar().apply {
            title.value = "Sample"
        }
        appBar.Compose()
    }
}


class Drawer(private val menus: List<String>, initialSelection: String)
{
    val selected = mutableStateOf(initialSelection)

    var onDrawerClose: (() -> Unit)? = null
    var onSelectionChange: ((String) -> Unit)? = null

    @Composable
    fun Compose()
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
        ) {
            IconButton(onClick = { onDrawerClose?.invoke() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
            for (menu in menus)
            {
                DrawerMenu(text = menu,
                    isSelected = menu == selected.value,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            selected.value = menu
                            onSelectionChange?.invoke(selected.value)
                        }
                )
            }
        }
    }

    @Composable
    fun DrawerMenu(text: String, isSelected: Boolean, modifier: Modifier = Modifier)
    {
        val textColor: Color
        val fontWeight: FontWeight
        if (isSelected)
        {
            textColor = MaterialTheme.colors.primary
            fontWeight = FontWeight.Bold
        } else
        {
            textColor = MaterialTheme.colors.onBackground
            fontWeight = FontWeight.Normal
        }

        Column(modifier = modifier) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = fontWeight,
                color = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color = textColor.copy(alpha = 0.25f))
            )
        }
    }
}

@Preview
@Composable
fun PrevDrawerMenu()
{
    val menus = Drawer(listOf("Home", "Youtube", "Facebook", "Instagram", "My Downloads"), "Home")
    MyappTheme {
        menus.Compose()
    }
}










