package com.gd.reelssaver.ui.router

import android.os.Parcelable

interface TabsNavigator<C : Parcelable> {
    fun navigate(
        transform: (state: TabNavState<C>) -> TabNavState<C>,
        onComplete: () -> Unit
    )
}


fun <C: Parcelable>TabsNavigator<C>.select(index: Int, onComplete: (() -> Unit)? = null) {
    navigate(
        onComplete = onComplete ?: {},
        transform = {state ->
            TabNavState(state.tabs, state.tabs[index])
        }
    )
}

fun <C: Parcelable>TabsNavigator<C>.addTab(tabConfig: C, onComplete: (() -> Unit)? = null) {
    navigate(
        onComplete = onComplete ?: {},
        transform = {state ->
            val tabs = state.tabs + tabConfig
            TabNavState(tabs, tabConfig)
        }
    )
}


fun <C: Parcelable>TabsNavigator<C>.remove(tabConfig: C, onComplete: (() -> Unit)? = null) {
    navigate(
        onComplete = onComplete ?: {},
        transform = {state ->
            val tabs = state.tabs - tabConfig
            TabNavState(tabs, tabConfig)
        }
    )
}


