package com.gd.reelssaver.ui.navigation

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.ui.model.Tab


interface BottomSheetComponent {
    val tabs: Value<List<Tab>>
    val activeTab: Value<Tab>
    val views: Map<String, WebView>
    fun onEvent(event: Event)

    sealed class Event {
        data object DismissBottomSheet : Event()
        data object ClearAllTabs : Event()
        data object AddNewTab : Event()
        data class RemoveTab(val tab: Tab) : Event()
        data class SelectTab(val tab: Tab) : Event()
        data object BackClick : Event()
        data object ForwardClick : Event()
    }
}

class DefaultBottomSheetComponent(
    componentContext: ComponentContext,
    override val activeTab: Value<Tab>,
    override val tabs: Value<List<Tab>>,
    override val views: Map<String, WebView>,
    private val onBottomSheetClose: () -> Unit,
    private val onAddNewTab: () -> Unit,
    private val onClearAllTab: () -> Unit,
    private val onRemoveTab: (Tab) -> Unit,
    private val onSelectTab: (Tab) -> Unit,
    private val onBackClick: () -> Unit,
    private val onForwardClick: () -> Unit
) : BottomSheetComponent, ComponentContext by componentContext {
    override fun onEvent(event: BottomSheetComponent.Event) {
        when (event) {
            is BottomSheetComponent.Event.DismissBottomSheet -> onBottomSheetClose()
            BottomSheetComponent.Event.AddNewTab -> onAddNewTab()
            BottomSheetComponent.Event.ClearAllTabs -> onClearAllTab()
            is BottomSheetComponent.Event.RemoveTab -> onRemoveTab(event.tab)
            BottomSheetComponent.Event.BackClick -> onBackClick()
            BottomSheetComponent.Event.ForwardClick -> onForwardClick()
            is BottomSheetComponent.Event.SelectTab -> onSelectTab(event.tab)
        }
    }
}