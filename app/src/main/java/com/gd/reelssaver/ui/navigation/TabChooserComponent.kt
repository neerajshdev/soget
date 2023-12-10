package com.gd.reelssaver.ui.navigation

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.model.Tab
import kotlinx.coroutines.flow.StateFlow


interface TabChooserComponent {
    val tabs: Value<List<Tab>>
    val activeTab: StateFlow<Tab?>
    val views: Map<String, WebView>
    fun onEvent(event: Event)

    sealed class Event {
        data object ClearAllTabs : Event()
        data object AddNewTab : Event()
        data class RemoveTab(val tab: Tab) : Event()
        data class SelectTab(val tab: Tab) : Event()
        data object BackClick : Event()
        data object ForwardClick : Event()
    }
}

class DefaultTabChooserComponent(
    componentContext: ComponentContext,
    override val activeTab: StateFlow<Tab?>,
    override val tabs: Value<List<Tab>>,
    override val views: Map<String, WebView>,
    private val onAddNewTab: () -> Unit,
    private val onClearAllTab: () -> Unit,
    private val onRemoveTab: (Tab) -> Unit,
    private val onSelectTab: (Tab) -> Unit,
    private val onBackClick: () -> Unit,
    private val onForwardClick: () -> Unit
) : TabChooserComponent, ComponentContext by componentContext {
    override fun onEvent(event: TabChooserComponent.Event) {
        when (event) {
            TabChooserComponent.Event.AddNewTab -> onAddNewTab()
            TabChooserComponent.Event.ClearAllTabs -> onClearAllTab()
            is TabChooserComponent.Event.RemoveTab -> onRemoveTab(event.tab)
            TabChooserComponent.Event.BackClick -> onBackClick()
            TabChooserComponent.Event.ForwardClick -> onForwardClick()
            is TabChooserComponent.Event.SelectTab -> onSelectTab(event.tab)
        }
    }
}