package com.gd.reelssaver.ui.screens.browser.tab.pages.homepage

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.ui.callbacks.OnOpenWebSite
import com.gd.reelssaver.ui.callbacks.OnTabChooserOpen
import com.gd.reelssaver.ui.callbacks.OnThemeToggle
import com.gd.reelssaver.util.Events
import java.net.URL


interface HomePageComponent : Events<Event> {
    val isDarkTheme: Value<Boolean>
    val tabsCount: Value<Int>
    val inputText: Value<String>
}

sealed interface Event {
    data class OnOpenWebSite(val url: URL) : Event
    data class UpdateInputText(val newValue: String) : Event
    data class SearchWeb(val query: String) : Event
    data object OpenTabChooser : Event
    data object ToggleTheme : Event
}


class DefaultHomePageComponent(
    context: ComponentContext,
    override val tabsCount: Value<Int>,
    override val isDarkTheme: Value<Boolean>,
    private val callback: HomepageComponentCallback
) : HomePageComponent, ComponentContext by context {

    private val _inputText = MutableValue("")
    override val inputText: Value<String> = _inputText
    override fun onEvent(e: Event) {
        when (e) {
            is Event.UpdateInputText -> _inputText.value = e.newValue
            is Event.OpenTabChooser -> callback.onTabChooserOpen()
            is Event.OnOpenWebSite -> callback.onOpenWebsite(e.url)
            is Event.SearchWeb -> callback.onOpenWebsite(URL("https://www.google.com/search?q=${e.query}"))
            is Event.ToggleTheme -> callback.onThemeToggle()
        }
    }
}

interface HomepageComponentCallback : OnThemeToggle, OnTabChooserOpen, OnOpenWebSite

class FakeHomePage : HomePageComponent {
    override val isDarkTheme: Value<Boolean> = MutableValue(false)
    override val tabsCount: Value<Int> = MutableValue(4)


    private val _inputText = MutableValue("")
    override val inputText: Value<String> = _inputText

    override fun onEvent(e: Event) {
        when (e) {
            is Event.UpdateInputText -> _inputText.value = e.newValue
            Event.OpenTabChooser -> TODO()
            is Event.OnOpenWebSite -> TODO()
            is Event.SearchWeb -> TODO()
            Event.ToggleTheme -> TODO()
        }
    }
}


