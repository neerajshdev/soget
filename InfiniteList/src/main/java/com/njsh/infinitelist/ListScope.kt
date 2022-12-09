package com.njsh.infinitelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ListScope<T>(
    private val config: Configuration<T> = Configuration(),
    private val scope: CoroutineScope
) {
    private val exceptionHandler = CoroutineExceptionHandler {coroutineContext, throwable ->
        throwable.printStackTrace()
    }

    class EndScope<T>(val f: LinkedList<T>, val l: LinkedList<T>)

    class Configuration<T> {
        lateinit var from: LinkedList<T>
        lateinit var to: LinkedList<T>
        lateinit var onEndOfFrame: suspend EndScope<T>.() -> Unit
        lateinit var composer: @Composable LayoutScope.(T) -> Unit
    }

    private var isLoading = false
    var scroll by mutableStateOf(0f)

    fun items(items: List<T>, block: @Composable LayoutScope.(T) -> Unit) {
        config.from = LinkedList.fromList(items)
        println("linked list => ${config.from.format()}, reverse: ${config.from.formatReverse()}")
        config.composer = block
    }

    fun onEndOfFrame(block: suspend EndScope<T>.() -> Unit) {
        config.onEndOfFrame = block
    }


    fun endFrame() {
        if (!isLoading) {
            scope.launch(exceptionHandler) {
                isLoading  = true
                config.onEndOfFrame(EndScope(config.from, config.to))
                isLoading = false
            }
        }
    }

    fun handleDrag(block: suspend PointerInputScope.() -> Unit) {

    }
}

class LayoutScope {
    private var block: (suspend LayoutInfo.() -> Unit)? = null

    fun onLayout(block: suspend LayoutInfo.() -> Unit) {
        this.block = block
    }

    suspend fun layout(layoutInfo: LayoutInfo) {
        block?.let { layoutInfo.it() }
    }
}

