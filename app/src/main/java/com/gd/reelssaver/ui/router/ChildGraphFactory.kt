package com.gd.reelssaver.ui.router

import android.os.Parcelable
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.children
import com.arkivanov.essenty.parcelable.ParcelableContainer
import com.arkivanov.essenty.parcelable.consumeRequired
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass


fun <C : Parcelable, T : Any> ComponentContext.childGraph(
    source: GraphNavigationSource<C>,
    key: String = "DefaultGraphNav",
    configClass: KClass<C>,
    persistable: Boolean = true,
    handleBackNav: Boolean = true,
    initialState: () -> GraphNavState<C>,
    childFactory: (config: C, ComponentContext) -> T
) = children(
    source = source,
    key = key,
    initialState = { initialState() },
    saveState = { state: GraphNavState<C> ->
        if (persistable) {
            ParcelableContainer(
                value = SavedGraphNavState(
                    state.graph.entries.associate {
                        Pair(ParcelableContainer(it.key), it.value.map { edge ->
                            Destination(
                                ParcelableContainer(edge.config), edge.type
                            )
                        })
                    }, ParcelableContainer(state.currKey)
                )
            )
        } else null
    },
    restoreState = { container: ParcelableContainer ->
        container.consumeRequired(SavedGraphNavState::class).let { savedState ->
            GraphNavState(
                graph = savedState.graph.entries.associate { item ->
                    val config = item.key.consumeRequired(configClass)
                    val edges = item.value.map { edge ->
                        Destination(edge.config.consumeRequired(configClass), edge.type)
                    }.toSet()
                    Pair(config, edges)
                }, currKey = savedState.currentKey.consumeRequired(configClass)
            )
        }
    },
    navTransformer = { state, event -> event.transform(state) },
    stateMapper = { state, children ->
        @Suppress("UNCHECKED_CAST") val createdChildren = children as List<Child.Created<C, T>>
        val active = createdChildren.first { child -> child.configuration == state.currKey }
        ChildGraph(active, state.graph)
    },
    onEventComplete = { event, _, _ -> event.onComplete() },
    backTransformer = { state ->
        if (!handleBackNav) return@children null
        val prevConfig = with(state) {
            graph[currKey]?.let { edges ->
                val edge = edges.firstOrNull { it.type == Destination.Direction.BACKWARD }
                edge?.config
            }
        }
        if (prevConfig != null) {
            { GraphNavState(state.graph, prevConfig) }
        } else null
    },
    childFactory = childFactory
)

@Parcelize
class SavedGraphNavState(
    val graph: Map<ParcelableContainer, List<Destination<ParcelableContainer>>>,
    val currentKey: ParcelableContainer
) : Parcelable


data class ChildGraph<C : Parcelable, T : Any>(
    val active: Child.Created<C, T>, val graph: Graph<C> // adjacent list
)