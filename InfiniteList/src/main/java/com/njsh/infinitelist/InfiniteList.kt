package com.njsh.infinitelist

import android.util.Log
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import kotlin.math.roundToInt

private const val TAG = "InfiniteList"

@Composable
fun <T : Any> InfiniteList(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    datasource: Datasource<T>,
    state: InfiniteListState = rememberInfiniteListState(),
    content: @Composable BoxScope.(T) -> Unit
) {
    SubcomposeLayout(
        modifier = modifier
            .fillMaxSize()
            .then(inputModifier(state.scrollHandler))
    ) { constraints ->

        Log.d(TAG, "SubcomposeLayout()")

        layout(constraints.maxWidth, constraints.maxHeight) {
            Log.d(TAG, "SubcomposeLayout().layout()")

            var tempScroll = state.scroll
            val itemsToPlace = mutableListOf<Pair<Placeable, Node<T>>>()
            val toFill = tempScroll + constraints.maxHeight


            fun composeNode(node: Node<T>): Placeable {
                val measurable = subcompose(slotId = node.index) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        content(node.data)
                    }
                }[0]
                return measurable.measure(constraints)
            }

            val startingFrom: Node<T> = datasource.left ?: datasource.createFirst()

            if (tempScroll < 0) {
                while (tempScroll < 0) {
                    if (startingFrom.index > 0) {
                        val p = startingFrom.prev ?: datasource.createPrev(startingFrom)
                        if (p != null) {
                            composeNode(p).also {
                                tempScroll += it.height
                                itemsToPlace.add(it to p)
                            }
                            datasource.left = p
                        } else {
                            tempScroll = 0f
                        }
                        continue
                    }
                    tempScroll = 0f
                }
            }

            var filled = 0
            var p: Node<T>? = startingFrom
            while (filled < toFill && p != null) {
                val placeable = composeNode(p)
                filled += placeable.height
                itemsToPlace.add(placeable to p)
                datasource.rightNode = p
                p = p.next ?: datasource.createNext(p)
            }

            val x = 0
            var y = -tempScroll.roundToInt()
            val visibleItems = mutableListOf<VisibleItems>()

            for (i in 0..itemsToPlace.lastIndex) {
                val item = itemsToPlace[i]
                val placeable = item.first
                val node = item.second

                if (placeable.height + y < 0) {
                    datasource.left = datasource.left?.next
                    tempScroll -= placeable.height
                    Log.d(
                        TAG,
                        "InfiniteList: visibleNodes: ${
                            checkNode(
                                datasource.left!!,
                                datasource.rightNode!!
                            )
                        }"
                    )

                    Log.d(
                        TAG,
                        "InfiniteList: nodes: ${
                            checkNode(
                                datasource.headNode!!,
                                datasource.tailNode!!
                            )
                        }"
                    )
                }

                item.first.place(x, y)
                y += placeable.height

                visibleItems.add(
                    VisibleItems(
                        node.index, node.data, placeable.width, placeable.height, x, y
                    )
                )
            }
            state.visibleItems = visibleItems
            state.scroll = tempScroll
        }
    }
}


private fun inputModifier(scrollHandler: VerticalScrollHandler) =
    Modifier.pointerInput(scrollHandler) {
        detectVerticalDragGestures(
            onDragStart = scrollHandler::onDragStart,
            onDragEnd = scrollHandler::onDragEnd,
            onVerticalDrag = scrollHandler::onVerticalDrag
        )
    }

private fun <T> checkNode(head: Node<T>, tail: Node<T>): String {
    val stringBuilder = StringBuilder()
    var current = head

    while (current != tail) {
        stringBuilder.append("node(${current.index}) ==> ")
        current = current.next ?: break
    }
    stringBuilder.append("node(${tail.index})")
    return stringBuilder.toString()
}