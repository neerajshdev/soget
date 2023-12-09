package com.gd.reelssaver.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.ui.model.Tab

interface WebScreenComponent {
    val tabs: Value<List<Tab>>
    val activeTab: Value<Tab>
    fun onEvent(event: Event)
    sealed class Event {
        data class UpdateUrl(val url: String) : Event()
    }
}


class DefaultWebScreenComponent(
    componentContext: ComponentContext,
    override val tabs: Value<List<Tab>>,
    override val activeTab: Value<Tab>,
    private val onTabUpdate: (old: Tab, new: Tab) -> Unit
) : WebScreenComponent, ComponentContext by componentContext {
    override fun onEvent(event: WebScreenComponent.Event) {
        when (event) {
            is WebScreenComponent.Event.UpdateUrl -> {
                onTabUpdate(activeTab.value, activeTab.value.copy(url = event.url))
            }
        }
    }
}


class FakeWebScreenComponent(): WebScreenComponent {
    override val tabs: Value<List<Tab>> = MutableValue(emptyList())
    override val activeTab: Value<Tab> = MutableValue(Tab(""))
    override fun onEvent(event: WebScreenComponent.Event) {
    }
}