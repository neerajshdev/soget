package com.njsh.infinitelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
    state: VerticleListState = rememberInfiniteListState(),
    block: ListScope<T>.() -> Unit
) {
    val exceptionHandler = remember {
        CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }
    }
    val map = remember {
        HashMap<LinkedList<T>, LayoutInfo>()
    }
    val scope = rememberCoroutineScope().also { state.assignScope(it) }
    val configuration = remember { ListScope.Configuration<T>() }
    val listScope = remember { ListScope(configuration, scope) } // do configuration
    listScope.block()

    SubcomposeLayout(modifier = modifier.then(state.inputModifier), measurePolicy = { constraints ->
        fun LayoutScope.subCompose(item: LinkedList<T>): Placeable {
            return subcompose(slotId = item.pos) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .wrapContentSize()
                ) {
                    configuration.composer(this@subCompose, item.value)
                }
            }[0].measure(constraints).also { state.itemDimension = it.height }
        }

        class PlaceableLayout {
            val layoutScope: LayoutScope = LayoutScope()
            lateinit var info: LayoutInfo
            lateinit var placeable: Placeable
            lateinit var item: LinkedList<T>

            inline fun place(block: (Placeable) -> LayoutInfo) {
                info = block(placeable)
                map[item] = info
            }

            fun tellOnLayout() {
                scope.launch(exceptionHandler) {
                    layoutScope.layout(info)
                }
            }
        }

        val viewport = Viewport(0, 0, constraints.maxWidth, constraints.maxHeight)
        state.dimension = viewport.height
        val startFrom = configuration.from
        val placeableLayouts = mutableListOf<PlaceableLayout>()

        val toFill = state.scroll + viewport.height
        var filled = 0
        var p = startFrom

        if (state.scroll < 0) {
            while (state.scroll < 0 && p.movePrev { p = it; configuration.from = it }) {
                val placeableLayout = PlaceableLayout()
                placeableLayouts.add(placeableLayout)
                placeableLayout.item = p
                placeableLayout.placeable = placeableLayout.layoutScope.subCompose(item = p)
                state.scroll += placeableLayout.placeable.height
            }
            state.scroll = state.scroll.coerceAtLeast(0f)
        }

        p = startFrom

        do {
            val placeableLayout = PlaceableLayout()
            placeableLayouts.add(placeableLayout)
            placeableLayout.item = p
            placeableLayout.placeable = placeableLayout.layoutScope.subCompose(item = p)
            filled += placeableLayout.placeable.height
        } while (filled < toFill && p.moveNext { p = it})

        configuration.to = p

        if (filled < toFill) {
            state.scroll = (state.scroll - toFill + filled).coerceAtLeast(0f)
        }

//        Log.d(TAG, "VerticalList: scroll = ${state.scroll}, itemDimen = ${state.itemDimension}")
        layout(viewport.width, viewport.height) {
            map.clear()
            val x = 0
            var y = -state.scroll.roundToInt()
            for (child in placeableLayouts) {
                child.place {
                    it.place(x, y)
                    LayoutInfo(x, y, it.width, it.height, viewport).also {
                        y += it.height
                    }
                }

                if (y <= 0) {
                    configuration.from.moveNext { configuration.from = it }
                    state.scroll -=child.info.height
                }

                child.tellOnLayout()
                listScope.endFrame()
            }
        }
    })
}




