package com.gd.infinitelist

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ListScope<T>(
    private val config: Configuration<T>,
    private val scope: CoroutineScope
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    class EndScope<T>(val f: LinkedList<T>, val l: LinkedList<T>)

    class Configuration<T> {
        lateinit var from: LinkedList<T>
        lateinit var to: LinkedList<T>
        var onEndOfFrame: (suspend EndScope<T>.() -> Unit)? = null
        lateinit var composer: @Composable LayoutScope.(T) -> Unit
    }

    fun items(items: List<T>, block: @Composable LayoutScope.(T) -> Unit) {
        config.from = LinkedList.fromList(items)
        println("linked list => ${config.from.format()}, reverse: ${config.from.formatReverse()}")
        config.composer = block
    }

    fun onEndOfFrame(block: suspend EndScope<T>.() -> Unit) {
        config.onEndOfFrame = block
    }


    fun endFrame() {
        scope.launch(exceptionHandler) {
            config.onEndOfFrame?.let { it(EndScope(config.from, config.to)) }
        }
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

