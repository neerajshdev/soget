package com.njsh.myapp.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import java.util.*

class PageNavigator(page: Page)
{
    private val stack: Stack<Page> = Stack<Page>()
    private val currentPage: MutableState<Page>

    init
    {
        stack.push(page)
        currentPage = mutableStateOf(stack.peek())
    }

    private fun updatePageState()
    {
        currentPage.value = stack.peek()
    }



    fun push(page: Page)
    {
        stack.push(page)
        updatePageState()
    }

    fun pop()
    {
        if(stack.size > 1)
        {
            stack.pop()
            updatePageState()
        }
    }

    fun replace(page: Page)
    {
        try
        {
            stack.pop()
        } catch (ignore: Exception) { }

        stack.push(page)
        updatePageState()
    }

    @Composable
    fun Compose()
    {
        Crossfade(targetState = currentPage.value) {
            it.content()
        }
    }
}

open class Page(
    val tag: String = ""
)
{
    var content: @Composable () -> Unit = {}

    fun addContent(content: @Composable () -> Unit)
    {
        this.content = content
    }
}