package com.njsh.infinitelist

import android.util.Log
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val TAG = "InfiniteList"

interface UniqueKey {
    val key: Any
}

@Composable
fun <T : UniqueKey> InfiniteList(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    datasource: Datasource<T>,
    state: InfiniteListState = rememberInfiniteListState(),
    content: @Composable BoxScope.(T) -> Unit
) {
    var first = remember {
        LinkedList.fromList(
            datasource.onFreshData()
        )
    }

    state.assignScope(rememberCoroutineScope())
    SubcomposeLayout(
        modifier = modifier
            .fillMaxSize()
            .then(state.inputModifier)
    ) { constraints ->

        state.dimension = constraints.maxHeight

        layout(constraints.maxWidth, constraints.maxHeight) {
            var tempScroll = state.scroll
            val itemsToPlace = mutableListOf<Pair<Placeable, LinkedList<T>>>()
            val toFill = tempScroll + constraints.maxHeight
            val startingFrom: LinkedList<T> = first
            val maxCacheSize = 10


            fun composeNode(node: LinkedList<T>): Placeable {
                Log.d(TAG, "composeNode: key = ${node.value.key}")
                val measurable = subcompose(slotId = node.value.key) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        content(node.value)
                    }
                }[0]
                return measurable.measure(constraints)
            }

            fun loadPrev(node: LinkedList<T>) {
                if (node.isFront()) {
                    datasource.onPrevOf(node.value).let {
                        node.addFront(it)
                        while (node.size > maxCacheSize) {
                            node.head.remove()
                        }
                    }
                }
            }

            fun loadNext(node: LinkedList<T>) {
                if (node.isEnd()) {
                    datasource.onNextOf(node.value).let {
                        node.add(it)
                        while(node.size > maxCacheSize) {
                            node.tail.removeFront()
                        }
                    }
                }
            }


            var p: LinkedList<T>? = startingFrom
            while (tempScroll < 0) {
                loadPrev(p!!)
                p = p.prev
                if (p != null) {
                    composeNode(p).also {
                        tempScroll += it.height
                        itemsToPlace.add(it to p!!)
                    }
                    first = p
                } else {
                    tempScroll = 0f
                }
            }

            var filled = 0
            p = startingFrom
            while (filled < toFill) {
                val placeable = composeNode(p!!)
                filled += placeable.height
                itemsToPlace.add(placeable to p)
                loadNext(p)
                p = p.next ?: break
            }

            Log.d(
                TAG,
                "InfiniteList: used(${first.value.key} to ${p!!.value.key}):  ${first.format()}"
            )

            val x = 0
            var y = -tempScroll.roundToInt()
            val visibleItems = mutableListOf<VisibleItems>()

            for (i in 0..itemsToPlace.lastIndex) {
                val item = itemsToPlace[i]
                val placeable = item.first
                val node = item.second

                if (placeable.height + y < 0) {
                    first = first.next!!
                    tempScroll -= placeable.height
                }

                item.first.place(x, y)
                y += placeable.height

                visibleItems.add(
                    VisibleItems(
                        node.value, placeable.width, placeable.height, x, y
                    )
                )
            }
            state.scope.launch {
                state.visibleItems = visibleItems
                state.scroll = tempScroll
                state.fireListeners()
            }
        }
    }
}


