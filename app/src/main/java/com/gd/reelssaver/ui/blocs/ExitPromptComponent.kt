package com.gd.reelssaver.ui.blocs

import com.arkivanov.decompose.ComponentContext

interface ExitPromptComponent {

    fun onEvent(event: Event)
    sealed class Event {
        data object ConfirmExit : Event()
        data object CancelExit : Event()
    }
}


class DefaultExitPromptComponent(
    componentContext: ComponentContext,
    val onExitConfirm: () -> Unit,
    val onExitCancel: () -> Unit

) : ComponentContext by componentContext, ExitPromptComponent {

    override fun onEvent(event: ExitPromptComponent.Event) {
        when (event) {
            ExitPromptComponent.Event.CancelExit -> onExitCancel()
            ExitPromptComponent.Event.ConfirmExit -> onExitConfirm()
        }
    }
}