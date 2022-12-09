package com.njsh.infinitelist

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "InfiniteList"

@Composable
fun <T> VerticalList(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    state: InfiniteListState = rememberInfiniteListState(),
    block: ListScope<T>.() -> Unit
) {
    val exceptionHandler = remember {
        CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }
    }
    val scope = rememberCoroutineScope().also { state.assignScope(it) }
    val configuration = remember { ListScope.Configuration<T>() }
    val infiniteListScope = remember { ListScope(configuration, scope) } // do configuration
    infiniteListScope.block()

    val pointerInput = Modifier.pointerInput(infiniteListScope) {
        detectVerticalDragGestures { change, dragAmount ->

        }
    }

    SubcomposeLayout(modifier = modifier.then(pointerInput), measurePolicy = { constraints ->
        fun LayoutScope.subCompose(item: LinkedList<T>): Placeable {
            return subcompose(slotId = item.pos) {
                Box(modifier = Modifier.padding(paddingValues).wrapContentSize()) {
                    configuration.composer(this@subCompose, item.value)
                }
            }[0].measure(constraints)
        }

        class PlaceableLayout {
            val layoutScope: LayoutScope = LayoutScope()
            lateinit var info: LayoutInfo
            lateinit var placeable: Placeable

            inline fun place(block: (Placeable) -> LayoutInfo) {
                info = block(placeable)
            }

            fun tellOnLayout() {
                scope.launch(exceptionHandler) {
                    layoutScope.layout(info)
                }
            }
        }

        val viewport = Viewport(0, 0, constraints.maxWidth, constraints.maxHeight)
        val startFrom = configuration.from
        val placeableLayouts = mutableListOf<PlaceableLayout>()

        val toFill = infiniteListScope.scroll + viewport.height
        var filled = 0
        var p = startFrom

        if (infiniteListScope.scroll < 0) {
            while (infiniteListScope.scroll < 0 && p.movePrev { p = it; configuration.from = it }) {
                val placeableLayout = PlaceableLayout()
                placeableLayouts.add(placeableLayout)
                placeableLayout.placeable = placeableLayout.layoutScope.subCompose(item = p)
                infiniteListScope.scroll += placeableLayout.placeable.height
            }
            infiniteListScope.scroll = infiniteListScope.scroll.coerceAtLeast(0f)
        }

        p = startFrom

        do {
            val placeableLayout = PlaceableLayout()
            placeableLayouts.add(placeableLayout)
            placeableLayout.placeable = placeableLayout.layoutScope.subCompose(item = p)
            filled += placeableLayout.placeable.height
        } while (filled < toFill && p.moveNext { p = it; configuration.to = it })

        if (filled < toFill) {
            infiniteListScope.scroll = (infiniteListScope.scroll - toFill + filled).coerceAtLeast(0f)
        }

        layout(viewport.width, viewport.height) {
            val x = 0
            var y = -infiniteListScope.scroll.roundToInt()
            for (child in placeableLayouts) {
                child.place {
                    it.place(x, y)
                    LayoutInfo(x, y, it.width, it.height, viewport).also {
                        y += it.height
                    }
                }

                child.tellOnLayout()
            }
        }
    })
}


