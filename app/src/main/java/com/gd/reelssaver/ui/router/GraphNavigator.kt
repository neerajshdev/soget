package com.gd.reelssaver.ui.router

import android.os.Parcelable
import okhttp3.internal.toImmutableMap

interface GraphNavigator<C : Parcelable> {
    fun navigate(
        transform: (state: GraphNavState<C>) -> GraphNavState<C>,
        onComplete: () -> Unit
    )
}

fun <C : Parcelable> GraphNavigator<C>.goto(destinationConfig: C, onComplete: () -> Unit) {
    val transform = { state: GraphNavState<C> ->
        updateGraphForGotoNavigation(state, destinationConfig)
    }
    navigate(transform, onComplete)
}

private fun <C : Parcelable> updateGraphForGotoNavigation(
    state: GraphNavState<C>,
    destinationConfig: C
): GraphNavState<C> {
    val newConfig = state.graph.keys.find { config -> config == destinationConfig }
    return newConfig?.let {
        GraphNavState(state.graph, newConfig)
    } ?: state
}

fun <C : Parcelable> GraphNavigator<C>.goForward(destinationConfig: C, onComplete: () -> Unit) {
    val transform: (GraphNavState<C>) -> GraphNavState<C> = { state ->
        updateGraphForForwardNavigation(state, destinationConfig)
    }
    navigate(transform, onComplete)
}

private fun <C : Parcelable> updateGraphForForwardNavigation(
    state: GraphNavState<C>,
    destinationConfig: C
): GraphNavState<C> {
    val prevKey = state.currKey
    val prevKeyEdges =
        state.graph[prevKey]?.plus(Destination(destinationConfig, Destination.Direction.FORWARD))

    val newKeyEdges = setOf(Destination(prevKey, Destination.Direction.BACKWARD))

    val newGraph = state.graph.toMutableMap().apply {
        if (prevKeyEdges != null) this[prevKey] = prevKeyEdges
        this[destinationConfig] = newKeyEdges
    }.toImmutableMap()

    return GraphNavState(newGraph, destinationConfig)
}


fun <C : Parcelable> GraphNavigator<C>.goBackward(destinationConfig: C, onComplete: () -> Unit) {
    val transform: (GraphNavState<C>) -> GraphNavState<C> = { state ->
        updateGraphForBackwardNavigation(state)
    }
    navigate(transform, onComplete)
}

private fun <C : Parcelable> updateGraphForBackwardNavigation(
    state: GraphNavState<C>,
): GraphNavState<C> {
    val prevConfig = state.currKey
    val destinationConfig =
        state.graph[prevConfig]?.find { edge -> edge.type == Destination.Direction.BACKWARD }?.config

    val newGraph = state.graph.toMutableMap()

    return destinationConfig?.let { config ->
        if (countIncomingDestination(state, destinationConfig) == 1) {
            val forwardConfigs = getForwardDestinations(state, prevConfig)
            forwardConfigs.forEach { newGraph.remove(it) }
        }
        GraphNavState(newGraph.toImmutableMap(), config)
    } ?: state
}

private fun <C : Parcelable> countIncomingDestination(
    state: GraphNavState<C>,
    targetConfig: C
): Int {
    return state.graph.values.flatMap { item ->
        item.filter { it.type == Destination.Direction.FORWARD && it.config == targetConfig }
    }.count()
}

private fun <C : Parcelable> getForwardDestinations(state: GraphNavState<C>, from: C): Set<C> {
    val set = mutableSetOf<C>()
    fun findRecursive(state: GraphNavState<C>, from: C, set: MutableSet<C>) {
        state.graph[from]?.let { destinations ->
            val configs =
                destinations.filter { it.type == Destination.Direction.FORWARD }.map { it.config }
            for (config in configs) {
                if (set.add(config)) findRecursive(state, config, set)
            }
        }
    }
    findRecursive(state, from, set)
    return set
}