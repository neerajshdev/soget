package com.gd.infinitelist

import androidx.compose.ui.input.pointer.PointerInputChange

class DragHandler(
    val onDragEnd: ((() -> Unit)?),
    val onDrag: ((PointerInputChange, Float) -> Unit)?,
) {
    class Lambdas {
        var onDrag: (() -> Unit)? = null
        var onDragEnd: ((PointerInputChange, Float) -> Unit)? = null
    }

    class Builder(private val lambdas: Lambdas) {
        fun onDragEnd(block: () -> Unit) {
            lambdas.onDrag = block
        }

        fun onDrag(block: (PointerInputChange, Float) -> Unit) {
            lambdas.onDragEnd = block
        }
    }
}