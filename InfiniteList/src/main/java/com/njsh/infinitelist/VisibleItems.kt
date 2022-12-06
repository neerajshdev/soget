package com.njsh.infinitelist

data class VisibleItems(
    val itemIndex: Int,
    val data: Any,
    val width: Int = 0,
    val height: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
)
