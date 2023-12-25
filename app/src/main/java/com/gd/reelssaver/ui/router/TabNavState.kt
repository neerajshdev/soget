package com.gd.reelssaver.ui.router

import android.os.Parcelable
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState

data class TabNavState<C: Parcelable>(
    val tabs: List<C>,
    val selectIndex: Int
): NavState<C>{

    override val children: List<ChildNavState<C>> = tabs.mapIndexed{ index, config ->
        SimpleChildNavState(
            configuration = config,
            status = if (index == selectIndex) ChildNavState.Status.ACTIVE else ChildNavState.Status.INACTIVE
        )
    }
}