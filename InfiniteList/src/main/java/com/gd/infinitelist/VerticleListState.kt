package com.gd.infinitelist

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class VerticleListState {
    private lateinit var _scope: CoroutineScope
    val scope: CoroutineScope by lazy { _scope }
    var scroll by mutableStateOf(0f)
    val visibleItemsObserver = mutableListOf<(List<VisibleItems>) -> Unit>()
    var dimension: Int = 0
    var itemDimension: Int = 0

    var visibleItems = emptyList<VisibleItems>()

    fun assignScope(scope: CoroutineScope) {
        _scope = scope.apply {
            this.plus(CoroutineExceptionHandler { coroutineContext, throwable ->
                throwable.printStackTrace()
            })
        }
    }

    fun scrollBy(delta: Float) {
        scroll += delta
    }

    fun observeVisibleItems(observer: (List<VisibleItems>) -> Unit): (List<VisibleItems>) -> Unit {
        visibleItemsObserver.add(observer)
        return observer
    }

    val inputModifier = Modifier.pointerInput(this) {
        // tracks the velocity of the pointer input
        val velocityTracker = VelocityTracker()

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }

        detectVerticalDragGestures(onVerticalDrag = { change, dragAmount ->
            scope.launch(exceptionHandler) {
                scrollBy(-dragAmount)
                velocityTracker.addPointerInputChange(change)
            }
        }, onDragEnd = {
            scope.launch(exceptionHandler) {
                val v = -velocityTracker.calculateVelocity().y
                val threshold = 200f
                var target = scroll

                when {
                    v > threshold -> {
                        target = itemDimension + 1f
                    }

                    v < -threshold -> {
                        target -= target
                    }

                    target > itemDimension / 2f -> {
                        target = itemDimension + 0.1f
                    }

                    target < itemDimension -> {
                        target = 0f
                    }
                }

//                println("current scroll = $scroll, target = $target")
                var old = scroll
                Animatable(old).animateTo(target) {
                    scroll += value - old
                    old = value
                }
            }
        })
    }
}

@Composable
fun rememberInfiniteListState(): VerticleListState {
    return remember { VerticleListState() }
}

