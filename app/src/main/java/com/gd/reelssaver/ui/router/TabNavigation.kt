package com.gd.reelssaver.ui.router

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TabNavigation<C : Parcelable>(
    private val scope: CoroutineScope
) : TabNavigationSource<C>, TabsNavigator<C> {
    private val relay = Channel<TabNavigationSource.Event<C>>()
    private val observers = mutableListOf<(TabNavigationSource.Event<C>) -> Unit>()

    init {
        scope.launch {
            relay.receiveAsFlow().collect { event ->
                observers.forEach { observer ->
                    observer(event)
                }
            }
        }
    }

    override fun subscribe(observer: (TabNavigationSource.Event<C>) -> Unit) {
        observers.add(observer)
    }

    override fun unsubscribe(observer: (TabNavigationSource.Event<C>) -> Unit) {
        observers.remove(observer)
    }

    override fun navigate(
        transform: (state: TabNavState<C>) -> TabNavState<C>,
        onComplete: () -> Unit
    ) {
        scope.launch {
            relay.send(TabNavigationSource.Event(transform, onComplete))
        }
    }
}