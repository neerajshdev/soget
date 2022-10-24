package com.njsh.instadl.util

interface CallResult<T : Any> {
    fun onSuccess(data: T)
    fun onFailed(ex: Exception)
}