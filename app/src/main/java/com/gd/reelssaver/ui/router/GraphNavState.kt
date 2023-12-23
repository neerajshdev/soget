package com.gd.reelssaver.ui.router

import android.os.Parcelable
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.children.NavState
import com.arkivanov.decompose.router.children.SimpleChildNavState


typealias Graph<C> = Map<C, Set<Destination<C>>>
data class GraphNavState<C: Parcelable>(
    val graph: Graph<C>, val currKey: C
): NavState<C>{
    init {
        require(graph.keys.isNotEmpty())
    }

    override val children: List<ChildNavState<C>> = graph.keys.map { config ->
        SimpleChildNavState(
            configuration = config,
            status = if (config == currKey) ChildNavState.Status.ACTIVE else ChildNavState.Status.INACTIVE
        )
    }
}