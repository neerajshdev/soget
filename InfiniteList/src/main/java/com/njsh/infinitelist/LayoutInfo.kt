package com.njsh.infinitelist

data class LayoutInfo(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val viewport: Viewport
)

data class Viewport(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
)


