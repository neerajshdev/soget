package com.gd.reelssaver.ui.blocs

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface TabChooserComponent {
    val pages: Flow<List<RootComponent.WebPageModel>>
    val selectedPage: StateFlow<RootComponent.WebPageModel?>
    fun onEvent(event: Event)

    sealed class Event {
        data object ClearAllTabs : Event()
        data object AddNewTab : Event()
        data class RemovePage(val id: String) : Event()
        data class SelectPage(val id: String) : Event()
        data object BackClick : Event()
        data object ForwardClick : Event()
    }
}

class DefaultTabChooserComponent(
    componentContext: ComponentContext,
    override val selectedPage: StateFlow<RootComponent.WebPageModel?>,
    override val pages: Flow<List<RootComponent.WebPageModel>>,
    private val onAddNewTab: () -> Unit,
    private val onClearAllPages: () -> Unit,
    private val onRemoveTab: (id: String) -> Unit,
    private val onSelectTab: (id: String) -> Unit,
    private val onBackClick: () -> Unit,
    private val onForwardClick: () -> Unit
) : TabChooserComponent, ComponentContext by componentContext {
    override fun onEvent(event: TabChooserComponent.Event) {
        when (event) {
            TabChooserComponent.Event.AddNewTab -> onAddNewTab()
            TabChooserComponent.Event.ClearAllTabs -> onClearAllPages()
            is TabChooserComponent.Event.RemovePage -> onRemoveTab(event.id)
            TabChooserComponent.Event.BackClick -> onBackClick()
            TabChooserComponent.Event.ForwardClick -> onForwardClick()
            is TabChooserComponent.Event.SelectPage -> onSelectTab(event.id)
        }
    }
}