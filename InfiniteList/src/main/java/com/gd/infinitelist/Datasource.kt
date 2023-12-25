package com.gd.infinitelist

private const val TAG = "Datasource"

abstract class Datasource<T> {
    private var maxSize: Int = 20

    fun setMaxCacheSize(max: Int) {
        assert(max > 0)
        maxSize = max
    }

    abstract suspend fun onFreshData(): List<T>
    abstract suspend fun onNextOf(item: T): List<T>
    abstract suspend fun onPrevOf(item: T): List<T>
}