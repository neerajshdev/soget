package com.njsh.infinitelist

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus

class InfiniteListState {
    private lateinit var _scope: CoroutineScope
    val scope: CoroutineScope by lazy { _scope }
    var scroll by mutableStateOf(0f)
    val scrollHandler: VerticalScrollHandler = DefaultVerticalScrollHandler(this)

    var visibleItems = emptyList<VisibleItems>()

    fun assignScope(scope: CoroutineScope) {
        _scope = scope.apply {
            this.plus(CoroutineExceptionHandler() { coroutineContext, throwable ->
                throwable.printStackTrace()
            })
        }
    }
}

@Composable
fun rememberInfiniteListState(): InfiniteListState {
    return remember { InfiniteListState() }
}

