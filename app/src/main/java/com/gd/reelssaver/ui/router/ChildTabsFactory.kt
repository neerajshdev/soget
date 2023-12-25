package com.gd.reelssaver.ui.router

import android.os.Parcelable
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.children
import com.arkivanov.essenty.parcelable.ParcelableContainer
import com.arkivanov.essenty.parcelable.consumeRequired
import com.gd.reelssaver.util.toKClass
import kotlinx.parcelize.Parcelize


fun <C : Parcelable, T : Any> ComponentContext.childTabs(
    source: TabNavigationSource<C>,
    key: String = "DefaultGraphNav",
    persistable: Boolean = true,
    initialTabs: List<C>,
    selectedTab: Int,
    childFactory: (config: C, ComponentContext) -> T
) = children(
    source = source,
    initialState = { TabNavState(tabs = initialTabs, selectIndex = selectedTab) },
    key = key,
    saveState = { state: TabNavState<C> ->
        if (persistable) {
            ParcelableContainer(
                value = SavedTabNavState(state.tabs, state.selectIndex)
            )
        } else null
    },
    restoreState = { container: ParcelableContainer ->
        val type = toKClass<SavedTabNavState<C>>()
        container.consumeRequired(type).let { savedState ->
            TabNavState(
                savedState.tabs,
                savedState.selectedTab
            )
        }
    },
    navTransformer = { state, event -> event.transform(state) },
    stateMapper = { state, children ->
        @Suppress("UNCHECKED_CAST") val createdChildren = children as List<Child.Created<C, T>>
        val selectedChild = with(state) { tabs[selectIndex] }
        val active = createdChildren.first { child -> selectedChild == child }
        ChildTabs(
            active,
            createdChildren.filter { it.configuration != selectedChild }
        )
    },
    onEventComplete = { event, _, _ -> event.onComplete() },
    backTransformer = { _ -> null },
    childFactory = childFactory
)

@Parcelize
class SavedTabNavState<C : Parcelable>(
    val tabs: List<C>,
    val selectedTab: Int
) : Parcelable


data class ChildTabs<C : Parcelable, T : Any>(
    val active: Child.Created<C, T>,
    val inActive: List<Child<C, T>>,
)


