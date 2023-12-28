package com.gd.reelssaver.ui.screens.splash

import com.arkivanov.decompose.ComponentContext
import com.gd.reelssaver.util.Events

interface SplashComponent : Events<Event> {
}

interface SplashComponentCallback {
    fun onSplashFinish()
}

sealed interface Event {
    data object Finish : Event
}

class DefaultSplashComponent(
    context: ComponentContext,
    val callback: SplashComponentCallback
) : ComponentContext by context,
    SplashComponent {

    override fun onEvent(e: Event) {
        when (e) {
            is Event.Finish -> callback.onSplashFinish()
        }
    }
}


class FakeSplashComponent : SplashComponent {
    override fun onEvent(e: Event) {
    }
}