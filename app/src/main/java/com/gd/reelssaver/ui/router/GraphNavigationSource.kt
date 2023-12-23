package com.gd.reelssaver.ui.router

import android.os.Parcelable
import com.arkivanov.decompose.router.children.NavigationSource

interface GraphNavigationSource<C : Parcelable> : NavigationSource<GraphNavigationSource.Event<C>> {
    data class Event<C : Parcelable>(
        val transform: (state: GraphNavState<C>) -> GraphNavState<C>,
        val onComplete: () -> Unit
    )
}