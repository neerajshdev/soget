package com.gd.reelssaver.ui.util

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope

interface ComponentScopeOwner {
    val scope: CoroutineScope
}

class DefaultComponentScopeOwner(context: ComponentContext): ComponentScopeOwner {
    override val scope: CoroutineScope = context.componentScope()
}