package com.njsh.infinitelist

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
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

class InfiniteListState {
    private lateinit var _scope: CoroutineScope
    val scope: CoroutineScope by lazy { _scope }
    var scroll by mutableStateOf(0f)
    val visibleItemsObserver = mutableListOf<(List<VisibleItems>) -> Unit>()
    var dimension: Int = 0
    var isScrolling = false

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

    fun observeVisibleItems(observer: (List<VisibleItems>) -> Unit): (List<VisibleItems>)->Unit {
        visibleItemsObserver.add(observer)
        return observer
    }

    val inputModifier = Modifier.pointerInput(this) {
        val velocityTracker = VelocityTracker()
        val decay = splineBasedDecay<Float>(this)

        detectVerticalDragGestures(
            onDragStart = { isScrolling = true },
            onVerticalDrag = { change, dragAmount ->
                scope.launch {
                    scrollBy(-dragAmount)
                    velocityTracker.addPointerInputChange(change)
                }
            },
            onDragEnd = {
                scope.launch {
                    val v = -velocityTracker.calculateVelocity().y
                    var target = decay.calculateTargetValue(scroll, v)
                    var diff = target - scroll

                    val force = 200

                    if (diff > 0) {
                        if (diff > force) {
                            target = visibleItems[0].height.toFloat() + 1
                        } else {
                            target = 0f
                        }
                    } else {
                        if (diff < -force) {
                            target = 0f
                        } else {
                            target = visibleItems[0].height.toFloat() + 1
                        }
                    }

                    var old = scroll
                    Animatable(old).animateTo(target) {
                        scroll += value - old
                        old = value
                    }
                    isScrolling = false
                }
            })
    }

    fun fireListeners() {
        if (!isScrolling) visibleItemsObserver.forEach {
            it(visibleItems)
        }
    }

    fun removeItemsObserver(observer: (List<VisibleItems>) -> Unit) {
        visibleItemsObserver.remove(observer)
    }
}

@Composable
fun rememberInfiniteListState(): InfiniteListState {
    return remember { InfiniteListState() }
}

