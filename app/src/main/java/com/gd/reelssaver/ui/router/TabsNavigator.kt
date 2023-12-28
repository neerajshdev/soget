package com.gd.reelssaver.ui.router

import android.os.Parcelable

interface TabsNavigator<C : Parcelable> {
    fun navigate(
        transform: (state: TabNavState<C>) -> TabNavState<C>,
        onComplete: () -> Unit
    )
}


fun <C : Parcelable> TabsNavigator<C>.select(index: Int, onComplete: (() -> Unit)? = null) {
    navigate(
        onComplete = onComplete ?: {},
        transform = { state ->
            assert(index in state.tabs.indices)
            TabNavState(state.tabs, index)
        }
    )
}

fun <C : Parcelable> TabsNavigator<C>.addTab(tabConfig: C, onComplete: (() -> Unit)? = null) {
    navigate(
        onComplete = onComplete ?: {},
        transform = { state ->
            val tabs = state.tabs + tabConfig
            TabNavState(tabs, tabs.lastIndex)
        }
    )
}


/**
 * Remove tab by index. index should be non selected tab
 */
fun <C : Parcelable> TabsNavigator<C>.remove(index: Int, onComplete: (() -> Unit)? = null) {
    navigate(
        onComplete = onComplete ?: {},
        transform = { state ->
            assert(index != state.selectIndex)
            val selectedTab = with(state) { tabs[selectIndex] }
            val tabs = state.tabs.toMutableList().apply {
                removeAt(index)
            }
            TabNavState(tabs, tabs.indexOf(selectedTab))
        }
    )
}

fun <C : Parcelable> TabsNavigator<C>.replaceAll(
    vararg config: C,
    selectIndex: Int = 0,
    onComplete: (() -> Unit)? = null
) {
    require(config.isNotEmpty())

    navigate(
        onComplete = onComplete ?: {},
        transform = {
            TabNavState(config.toList(), selectIndex)
        }
    )
}

