package com.gd.reelssaver.ui.router

import android.os.Parcelable
import com.arkivanov.decompose.router.children.NavigationSource

interface TabNavigationSource<C : Parcelable> : NavigationSource<TabNavigationSource.Event<C>> {
    data class Event<C : Parcelable>(
        val transform: (state: TabNavState<C>) -> TabNavState<C>,
        val onComplete: () -> Unit
    )
}