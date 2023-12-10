package com.gd.reelssaver.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.gd.reelssaver.model.Tab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URL


interface HomeScreenComponent {
    val useDarkTheme: StateFlow<Boolean>
    val inputText: Value<String>
    val tabCount: Value<Int>
    fun onEvent(event: Event)

    sealed class Event {
        data class OpenWeb(val url: URL) : Event()
        data class UpdateInputText(val text: String) : Event()
        data class SearchWeb(val query: String) : Event()

        data object OpenTabChooser: Event()

        data object ToggleTheme: Event()
    }
}

class DefaultHomeScreenComponent(
    componentContext: ComponentContext,
    tabs: Value<List<Tab>>,
    override val useDarkTheme: StateFlow<Boolean>,
    private val onOpenWebUrl: (URL) -> Unit,
    private val onOpenTabChooser: () -> Unit,
    private val onToggleTheme: () -> Unit
) : HomeScreenComponent, ComponentContext by componentContext {


    private val _inputText = MutableValue("")
    override val inputText: Value<String> = _inputText

    override val tabCount: Value<Int> = tabs.map { it.size }
    override fun onEvent(event: HomeScreenComponent.Event) {
        when (event) {
            is HomeScreenComponent.Event.OpenWeb -> {
                onOpenWebUrl(event.url)
            }

            is HomeScreenComponent.Event.UpdateInputText -> {
                _inputText.value = event.text
            }

            is HomeScreenComponent.Event.SearchWeb -> {
                val url = URL("https://www.google.com/search?q=${event.query}")
                onOpenWebUrl(url)
            }

            HomeScreenComponent.Event.OpenTabChooser -> onOpenTabChooser()
            HomeScreenComponent.Event.ToggleTheme -> onToggleTheme()
        }
    }
}


class FakeHomeScreenComponent : HomeScreenComponent {
    override val useDarkTheme: StateFlow<Boolean> = MutableStateFlow(false)

    override val inputText: Value<String> = MutableValue("")
    override val tabCount: Value<Int> = MutableValue(4)
    override fun onEvent(event: HomeScreenComponent.Event) {
        // ignore
    }
}


