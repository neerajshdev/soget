package com.njsh.myapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.*

class Navigator : Page()
{
    val stack: Stack<Page> = Stack<Page>()

    @Composable
    override fun Compose()
    {
    }
}


open class Page
{
    val modifier = Modifier

    @Composable
    open fun Compose()
    {
    }
}