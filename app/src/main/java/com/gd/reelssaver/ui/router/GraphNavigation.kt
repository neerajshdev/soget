package com.gd.reelssaver.ui.router

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GraphNavigation<C : Parcelable>(
    private val scope: CoroutineScope
) : GraphNavigationSource<C>, GraphNavigator<C> {
    private val relay = Channel<GraphNavigationSource.Event<C>>()
    private val observers = mutableListOf<(GraphNavigationSource.Event<C>) -> Unit>()

    init {
        scope.launch {
            relay.receiveAsFlow().collect { event ->
                observers.forEach { observer ->
                    observer(event)
                }
            }
        }
    }

    override fun subscribe(observer: (GraphNavigationSource.Event<C>) -> Unit) {
        observers.add(observer)
    }

    override fun unsubscribe(observer: (GraphNavigationSource.Event<C>) -> Unit) {
        observers.remove(observer)
    }

    override fun navigate(
        transform: (state: GraphNavState<C>) -> GraphNavState<C>,
        onComplete: () -> Unit
    ) {
        scope.launch {
            relay.send(GraphNavigationSource.Event(transform, onComplete))
        }
    }
}