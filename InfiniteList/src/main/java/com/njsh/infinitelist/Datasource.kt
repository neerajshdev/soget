package com.njsh.infinitelist

private const val TAG = "Datasource"

abstract class Datasource<T> {
    private var maxSize: Int = 20

    fun setMaxCacheSize(max: Int) {
        assert(max > 0)
        maxSize = max
    }

    abstract fun onFreshData(): List<T>
    abstract fun onNextOf(item: T): List<T>
    abstract fun onPrevOf(item: T): List<T>
}