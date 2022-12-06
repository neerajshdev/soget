package com.njsh.infinitelist

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange

abstract class VerticalScrollHandler {
   abstract fun onDragStart(offset: Offset)
   abstract fun onVerticalDrag(change: PointerInputChange, amt: Float)
   abstract fun onDragEnd()
}


class DefaultVerticalScrollHandler(val state: InfiniteListState) : VerticalScrollHandler() {
    override fun onDragStart(offset: Offset) {
    }

    override fun onVerticalDrag(change: PointerInputChange, amt: Float) {
        state.scroll -= amt
    }

    override fun onDragEnd() {
    }
}