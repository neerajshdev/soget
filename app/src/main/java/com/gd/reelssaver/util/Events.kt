package com.gd.reelssaver.util


/**
 * Interface for the screen ui content
 */
interface Events<E: Any> {
    fun onEvent(e: E)
}