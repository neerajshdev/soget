package com.njsh.reelssaver.api

sealed class CallResult<out T : Any> {
    class Success<out T : Any>(val data: T) : CallResult<T>()
    data class Failed(val msg: String) : CallResult<Nothing>()
}

